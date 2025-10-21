package momomomo.dungeonwalker.wsserver.core.connection;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.commons.DateTimeManager;
import momomomo.dungeonwalker.wsserver.core.config.HeartbeatConfig;
import momomomo.dungeonwalker.wsserver.core.connection.command.CloseConnection;
import momomomo.dungeonwalker.wsserver.core.connection.command.ConnectionCommand;
import momomomo.dungeonwalker.wsserver.core.connection.command.ResetConnection;
import momomomo.dungeonwalker.wsserver.core.connection.command.SendHeartbeatToClient;
import momomomo.dungeonwalker.wsserver.core.connection.command.SendMessageFromClient;
import momomomo.dungeonwalker.wsserver.core.connection.command.SetConnection;
import momomomo.dungeonwalker.wsserver.domain.connection.ClientConnection;
import momomomo.dungeonwalker.wsserver.domain.output.Heartbeat;
import momomomo.dungeonwalker.wsserver.domain.output.Output;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@Slf4j
public class ConnectionActor extends AbstractBehavior<ConnectionCommand> {

    private ClientConnection connection;
    private final DateTimeManager dateTimeManager;
    private final HeartbeatConfig heartbeatConfig;

    private ConnectionActor(
            final ActorContext<ConnectionCommand> context,
            final DateTimeManager dateTimeManager,
            final HeartbeatConfig heartbeatConfig) {
        super(context);
        this.dateTimeManager = dateTimeManager;
        this.heartbeatConfig = heartbeatConfig;
    }

    public static Behavior<ConnectionCommand> create(
            final DateTimeManager dateTimeManager,
            final HeartbeatConfig heartbeatConfig) {
        log.debug("[ACTOR - Connection] create");
        return Behaviors.setup(context -> new ConnectionActor(context, dateTimeManager, heartbeatConfig));
    }

    @Override
    public Receive<ConnectionCommand> createReceive() {
        log.debug("[ACTOR - Connection] create receive");
        return newReceiveBuilder()
                .onMessage(SetConnection.class, this::onSetConnection)
                .build();
    }

    public Receive<ConnectionCommand> connectionSet() {
        log.debug("[ACTOR - Connection] session connection");
        return newReceiveBuilder()
                .onMessage(ResetConnection.class, this::onResetConnection)
                .onMessage(CloseConnection.class, this::onCloseConnection)
                .onMessage(SendHeartbeatToClient.class, this::onSendHeartbeatToClient)
                .onMessage(SendMessageFromClient.class, this::onSendMessageFromClient)
                .build();
    }

    public Behavior<ConnectionCommand> connectionClosed() {
        log.debug("[ACTOR - Connection] connection closed:  \"{}\"", this.connection.getSessionId());
        return Behaviors.stopped();
    }

    private Behavior<ConnectionCommand> onSetConnection(final SetConnection command) {
        log.debug("[ACTOR - Connection] on set connection \"{}\":\"{}\"",
                command.connection().getUserId(),
                command.connection().getSessionId());

        this.connection = command.connection();

        return Behaviors.withTimers(timer -> {
            final var key = "timer-" + getContext().getSelf().path().name();
            log.debug("[ACTOR - Connection] setting timer \"{}\"", key);

            if (timer.isTimerActive(key)) {
                timer.cancel(key);
            }

            timer.startTimerWithFixedDelay(
                    key,
                    new SendHeartbeatToClient(),
                    Duration.of(heartbeatConfig.getDelay(), ChronoUnit.valueOf(heartbeatConfig.getTimeUnit())));

            return connectionSet();
        });
    }

    private Behavior<ConnectionCommand> onResetConnection(final ResetConnection command) {
        log.debug("[ACTOR - Connection] on set connection again \"{}\":\"{}\" vs. \"{}\":\"{}\"",
                connection.getUserId(),
                connection.getSessionId(),
                command.connection().getUserId(),
                command.connection().getSessionId());

        if (!Objects.equals(connection.getSessionId(), command.connection().getSessionId())) {
            log.warn("[ACTOR - Connection] Resetting connection since sessions has changed \"{}\":\"{}\" vs. \"{}\":\"{}\"",
                    connection.getUserId(),
                    connection.getSessionId(),
                    command.connection().getUserId(),
                    command.connection().getSessionId());
            connection.close();
            connection = command.connection();

        } else {
            log.warn("[ACTOR - Connection] Connection with same session ID will not be reset \"{}\":\"{}\"",
                    connection.getUserId(),
                    connection.getSessionId());
        }

        return Behaviors.same();
    }

    private Behavior<ConnectionCommand> onCloseConnection(final CloseConnection command) {
        log.debug("[ACTOR - Connection] on close connection \"{}\":\"{}\"",
                command.connection().getUserId(),
                command.connection().getSessionId());

        if (connection.getSessionId().equals(command.connection().getSessionId())) {
            log.debug("[ACTOR - Connection] closing connection \"{}\":\"{}\"",
                    command.connection().getUserId(),
                    command.connection().getSessionId());
            return connectionClosed();
        }

        return Behaviors.same();
    }

    private Behavior<ConnectionCommand> onSendHeartbeatToClient(final SendHeartbeatToClient command) {
        log.debug("[ACTOR - Connection] on send heartbeat to \"{}\":\"{}\"",
                connection.getUserId(),
                connection.getSessionId());

        final var output = Output.builder()
                .type("heartbeat")
                .data(Heartbeat.builder()
                        .timestamp(dateTimeManager.now())
                        .delay(heartbeatConfig.getDelay())
                        .timeUnit(heartbeatConfig.getTimeUnit())
                        .build())
                .build();

        connection.send(output);

        return Behaviors.same();
    }

    private Behavior<ConnectionCommand> onSendMessageFromClient(final SendMessageFromClient command) {
        log.debug("[ACTOR - Connection] on act on message \"{}\":\"{}\"",
                connection.getUserId(),
                connection.getSessionId());

        // Map input to protobuf
        // GameGateway.publish(protobuf)
        // Publish will send a message to a Kafka Topic

        return Behaviors.same();
    }

}

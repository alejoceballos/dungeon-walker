package momomomo.dungeonwalker.wsserver.core.sctor.connection;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.wsserver.core.sctor.connection.command.CloseConnection;
import momomomo.dungeonwalker.wsserver.core.sctor.connection.command.ConnectionCommand;
import momomomo.dungeonwalker.wsserver.core.sctor.connection.command.SendHeartbeatToClient;
import momomomo.dungeonwalker.wsserver.core.sctor.connection.command.SendMessageFromClient;
import momomomo.dungeonwalker.wsserver.core.sctor.connection.command.SetConnection;
import momomomo.dungeonwalker.wsserver.domain.inbound.ClientConnection;
import momomomo.dungeonwalker.wsserver.domain.output.Heartbeat;
import momomomo.dungeonwalker.wsserver.domain.output.Output;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;

import static momomomo.dungeonwalker.wsserver.core.handler.HandlingResult.type.FAILURE;

@Slf4j
public class ConnectionActor extends AbstractBehavior<ConnectionCommand> {

    private ClientConnection currentConnection;

    private ConnectionActor(@NonNull final ActorContext<ConnectionCommand> context) {
        super(context);
    }

    public static Behavior<ConnectionCommand> create() {
        log.debug("[ACTOR - Connection] create");
        return Behaviors.setup(ConnectionActor::new);
    }

    @Override
    public Receive<ConnectionCommand> createReceive() {
        log.debug("[ACTOR - Connection][{}] create receive", actorId());
        return newReceiveBuilder()
                .onMessage(SetConnection.class, this::onSetConnection)
                .build();
    }

    public Receive<ConnectionCommand> connected() {
        log.debug("[ACTOR - Connection][{}] session connection", actorId());
        return newReceiveBuilder()
                .onMessage(SetConnection.class, this::onResetConnection)
                .onMessage(CloseConnection.class, this::onCloseConnection)
                .onMessage(SendHeartbeatToClient.class, this::onSendHeartbeatToClient)
                .onMessage(SendMessageFromClient.class, this::onSendMessageFromClient)
                .build();
    }

    public Behavior<ConnectionCommand> connectionClosed() {
        log.debug("[ACTOR - Connection][{}] connection closed", actorId());
        return Behaviors.stopped();
    }

    private Behavior<ConnectionCommand> onSetConnection(final SetConnection command) {
        log.debug("[ACTOR - Connection][{}] on set connection \"{}\":\"{}\"",
                actorId(),
                command.connection().getUserId(),
                command.connection().getSessionId());

        this.currentConnection = command.connection();

        return Behaviors.withTimers(timer -> {
            final var key = "timer-" + getContext().getSelf().path().name();
            log.debug("[ACTOR - Connection][{}] setting timer \"{}\"", actorId(), key);

            if (timer.isTimerActive(key)) {
                timer.cancel(key);
            }

            timer.startTimerWithFixedDelay(
                    key,
                    new SendHeartbeatToClient(
                            command.connection(),
                            command.dateTimeManager(),
                            command.heartbeatConfig()),
                    Duration.of(
                            command.heartbeatConfig().getDelay(),
                            ChronoUnit.valueOf(command.heartbeatConfig().getTimeUnit())));

            return connected();
        });
    }

    private Behavior<ConnectionCommand> onResetConnection(final SetConnection command) {
        log.debug("[ACTOR - Connection][{}] on set connection again \"{}\":\"{}\" vs. \"{}\":\"{}\"",
                actorId(), userId(), sessionId(), command.connection().getUserId(), command.connection().getSessionId());

        if (!Objects.equals(currentConnection.getSessionId(), command.connection().getSessionId())) {
            log.warn("[ACTOR - Connection][{}] Resetting connection since sessions has changed \"{}\":\"{}\" vs. \"{}\":\"{}\"",
                    actorId(), userId(), sessionId(), command.connection().getUserId(), command.connection().getSessionId());
            currentConnection.close();
            currentConnection = command.connection();

        } else {
            log.warn("[ACTOR - Connection][{}] Connection with same session ID will not be reset \"{}\":\"{}\"",
                    actorId(), userId(), sessionId());
        }

        return Behaviors.same();
    }

    private Behavior<ConnectionCommand> onCloseConnection(final CloseConnection command) {
        log.debug("[ACTOR - Connection][{}] on close connection \"{}\":\"{}\"",
                actorId(), command.connection().getUserId(), command.connection().getSessionId());

        if (isSameConnection(command.connection())) {
            log.debug("[ACTOR - Connection][{}] closing connection \"{}\":\"{}\"",
                    actorId(), command.connection().getUserId(), command.connection().getSessionId());
            return connectionClosed();
        }

        return Behaviors.same();
    }

    private Behavior<ConnectionCommand> onSendHeartbeatToClient(final SendHeartbeatToClient command) {
        log.debug("[ACTOR - Connection][{}] on send heartbeat to \"{}\":\"{}\"",
                actorId(), command.connection().getUserId(), command.connection().getSessionId());

        final var output = Output.builder()
                .type("heartbeat")
                .data(Heartbeat.builder()
                        .timestamp(command.dateTimeManager().now())
                        .delay(command.heartbeatConfig().getDelay())
                        .timeUnit(command.heartbeatConfig().getTimeUnit())
                        .build())
                .build();

        command.connection().send(output);

        return Behaviors.same();
    }

    private Behavior<ConnectionCommand> onSendMessageFromClient(final SendMessageFromClient command) {
        log.debug("[ACTOR - Connection][{}]  on act on message \"{}\":\"{}\"",
                actorId(), command.connection().getUserId(), command.connection().getSessionId());

        final var result = command.dataHandlerSelector()
                .select(command.message().data())
                .handle(command.message().data());

        if (result.type().equals(FAILURE)) {
            log.error("[ACTOR - Connection][{}]  Message handling failed for user\"{}\":\"{}\" with result: {}",
                    actorId(), command.connection().getUserId(), command.connection().getSessionId(), result);
            // TODO: send error message back to client using another command
        }

        return Behaviors.same();
    }

    private String actorId() {
        return getContext().getSelf().path().name();
    }

    private String sessionId() {
        return Optional
                .ofNullable(currentConnection)
                .map(ClientConnection::getSessionId)
                .orElse(null);
    }

    private String userId() {
        return Optional
                .ofNullable(currentConnection)
                .map(ClientConnection::getUserId)
                .orElse(null);
    }

    private boolean isSameConnection(final ClientConnection connection) {
        return Objects.equals(sessionId(), connection.getSessionId());
    }

}

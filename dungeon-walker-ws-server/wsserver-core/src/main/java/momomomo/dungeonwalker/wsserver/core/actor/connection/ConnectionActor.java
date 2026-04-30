package momomomo.dungeonwalker.wsserver.core.actor.connection;

import jakarta.annotation.Nullable;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.commons.DateTimeManager;
import momomomo.dungeonwalker.contract.client.ClientRequestProto.ClientRequest;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.CloseConnection;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.ConnectionCommand;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.SendHeartbeatToClient;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.SetConnection;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.from.client.SendMessageToEngine;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.from.engine.SendWalkersPositionToClient;
import momomomo.dungeonwalker.wsserver.core.config.properties.heartbeat.HeartbeatProps;
import momomomo.dungeonwalker.wsserver.core.handler.client.DataHandlerSelector;
import momomomo.dungeonwalker.wsserver.domain.inbound.ClientConnection;
import momomomo.dungeonwalker.wsserver.domain.input.client.Input;
import momomomo.dungeonwalker.wsserver.domain.input.client.Leave;
import momomomo.dungeonwalker.wsserver.domain.outbound.Sender;
import momomomo.dungeonwalker.wsserver.domain.output.Output;
import momomomo.dungeonwalker.wsserver.domain.output.ServerErrors;
import momomomo.dungeonwalker.wsserver.domain.output.ServerHeartbeat;
import momomomo.dungeonwalker.wsserver.domain.output.ServerMessage;
import org.apache.pekko.actor.typed.ActorRef;
import org.apache.pekko.actor.typed.Behavior;
import org.apache.pekko.actor.typed.javadsl.AbstractBehavior;
import org.apache.pekko.actor.typed.javadsl.ActorContext;
import org.apache.pekko.actor.typed.javadsl.Behaviors;
import org.apache.pekko.actor.typed.javadsl.Receive;
import org.apache.pekko.actor.typed.pubsub.Topic;
import org.apache.pekko.cluster.sharding.typed.javadsl.EntityTypeKey;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.failedFuture;

@Slf4j
public class ConnectionActor extends AbstractBehavior<ConnectionCommand> {

    private static final String LABEL = "---> [ACTOR - Connection]";

    public static final EntityTypeKey<ConnectionCommand> ENTITY_TYPE_KEY =
            EntityTypeKey.create(ConnectionCommand.class, "connection-actor-type-key");

    private final DataHandlerSelector dataHandlerSelector;
    private final DateTimeManager dateTimeManager;
    private final HeartbeatProps heartbeatProps;
    private final Sender<ClientRequest> sender;

    private ClientConnection clientConnection;

    private ConnectionActor(
            @NonNull final ActorContext<ConnectionCommand> context,
            @NonNull final ActorRef<Topic.Command<ConnectionCommand>> connectionBroadcastTopic,
            @NonNull final DataHandlerSelector dataHandlerSelector,
            @NonNull final DateTimeManager dateTimeManager,
            @NonNull final HeartbeatProps heartbeatProps,
            @NonNull final Sender<ClientRequest> sender
    ) {
        super(context);
        this.dataHandlerSelector = dataHandlerSelector;
        this.dateTimeManager = dateTimeManager;
        this.heartbeatProps = heartbeatProps;
        this.sender = sender;

        connectionBroadcastTopic.tell(Topic.subscribe(context.getSelf()));
    }

    public static Behavior<ConnectionCommand> create(
            @NonNull final ActorRef<Topic.Command<ConnectionCommand>> connectionBroadcastTopic,
            @NonNull final DataHandlerSelector dataHandlerSelector,
            @NonNull final DateTimeManager dateTimeManager,
            @NonNull final HeartbeatProps heartbeatProps,
            @NonNull final Sender<ClientRequest> sender
    ) {
        log.debug("{} create", LABEL);
        return Behaviors.setup(context ->
                new ConnectionActor(
                        context,
                        connectionBroadcastTopic,
                        dataHandlerSelector,
                        dateTimeManager,
                        heartbeatProps,
                        sender));
    }

    @Override
    public Receive<ConnectionCommand> createReceive() {
        log.debug("{}[{}] create receive", LABEL, actorId());
        return newReceiveBuilder()
                .onMessage(SetConnection.class, this::onSetConnection)
                .build();
    }

    public Receive<ConnectionCommand> connected() {
        log.debug("{}[{}] session connection", LABEL, actorId());
        return newReceiveBuilder()
                .onMessage(SetConnection.class, this::onResetConnection)
                .onMessage(CloseConnection.class, this::onCloseConnection)
                .onMessage(SendHeartbeatToClient.class, this::onSendHeartbeatToClient)
                .onMessage(SendMessageToEngine.class, this::onSendMessageToEngine)
                .onMessage(SendWalkersPositionToClient.class, this::onSendWalkersPositionToClient)
                .build();
    }

    private Behavior<ConnectionCommand> onSetConnection(final SetConnection command) {
        clientConnection = command.connection();
        log.debug(logMessage("on set connection"));


        return Behaviors.withTimers(timer -> {
            final var timerHeartbeatKey = "timer-heartbeat-" + actorId();
            log.debug("{}[{}] setting timer \"{}\"", LABEL, actorPath(), timerHeartbeatKey);

            if (timer.isTimerActive(timerHeartbeatKey)) {
                timer.cancel(timerHeartbeatKey);
            }

            timer.startTimerWithFixedDelay(
                    timerHeartbeatKey,
                    new SendHeartbeatToClient(),
                    Duration.of(
                            heartbeatProps.getDelay(),
                            ChronoUnit.valueOf(heartbeatProps.getTimeUnit())));

            return connected();
        });
    }

    private Behavior<ConnectionCommand> onResetConnection(@NonNull final SetConnection command) {
        final var cmdUserId = command.connection().getUserId();
        final var cmdSessionId = command.connection().getSessionId();

        log.debug("{}[{}] on set connection again \"{}\":\"{}\" vs. \"{}\":\"{}\"",
                LABEL, actorPath(), userId(), sessionId(), cmdUserId, cmdSessionId);

        if (!Objects.equals(clientConnection.getSessionId(), cmdSessionId)) {
            log.warn("{}[{}] Resetting connection since sessions has changed \"{}\":\"{}\" vs. \"{}\":\"{}\"",
                    LABEL, actorPath(), userId(), sessionId(), cmdUserId, cmdSessionId);
            clientConnection.close();
            clientConnection = command.connection();

        } else {
            log.warn("{}[{}] Connection with same session ID will not be reset \"{}\":\"{}\"",
                    LABEL, actorPath(), userId(), sessionId());
        }

        return Behaviors.same();
    }

    private Behavior<ConnectionCommand> onCloseConnection(@NonNull final CloseConnection command) {
        final var cmdSessionId = command.connection().getSessionId();

        log.debug("{}[{}] on close connection \"{}\":\"{}\"", LABEL, actorPath(), command.connection().getUserId(), cmdSessionId);

        if (isSameConnection(command.connection())) {
            log.debug("{}[{}] closing connection \"{}\":\"{}\"", LABEL, actorPath(), command.connection().getUserId(), cmdSessionId);

            return Behaviors.withTimers(timer -> {
                final var timerHeartbeatKey = "timer-heartbeat-" + actorId();

                if (timer.isTimerActive(timerHeartbeatKey)) {
                    log.debug("{}[{}] cancelling active {} timer due to connection close \"{}\":\"{}\"",
                            LABEL, actorPath(), timerHeartbeatKey, command.connection().getUserId(), cmdSessionId);
                    timer.cancel(timerHeartbeatKey);

                } else {
                    log.debug("{}[{}] {} timer not active. Continue to close connection \"{}\":\"{}\"",
                            LABEL, actorPath(), timerHeartbeatKey, command.connection().getUserId(), cmdSessionId);
                }

                getContext().getSelf().tell(new SendMessageToEngine(Input.of(new Leave(userId()))));

                return Behaviors.stopped();
            });
        }

        return Behaviors.same();
    }

    private Behavior<ConnectionCommand> onSendHeartbeatToClient(@NonNull final SendHeartbeatToClient command) {
        log.debug(logMessage("on send heartbeat to"));

        final var output = Output.of(
                new ServerHeartbeat(
                        heartbeatProps.getDelay(),
                        heartbeatProps.getTimeUnit(),
                        dateTimeManager.instantNow()));

        clientConnection.send(output);

        return Behaviors.same();
    }

    private Behavior<ConnectionCommand> onSendMessageToEngine(@NonNull final SendMessageToEngine command) {
        log.debug(logMessage("on act on message"));

        final var data = command
                .message()
                .cloneWith(userId())
                .data();

        dataHandlerSelector
                .select(data)
                .handle(data, sender)
                .thenCompose(result -> {
                    switch (result.type()) {
                        case FAILURE -> {
                            log.error("{}[{}] Failure sending message for user \"{}\":\"{}\". Result: {}",
                                    LABEL, actorPath(), userId(), sessionId(), result);
                            clientConnection.send(Output.of(new ServerErrors(result.errors())));
                        }
                        case SUCCESS -> {
                            log.debug("{}[{}] Success sending message for user \"{}\":\"{}\"",
                                    LABEL, actorPath(), userId(), sessionId());
                            clientConnection.send(Output.of(new ServerMessage("Message sent")));
                        }
                        default -> {
                            log.debug("{}[{}] Ignoring message for user \"{}\":\"{}\"",
                                    LABEL, actorPath(), userId(), sessionId());
                            clientConnection.send(Output.of(new ServerMessage("Message ignored")));
                        }
                    }

                    return CompletableFuture.completedFuture(null);

                }).exceptionally(ex -> {
                    log.error("{}[{}] Error sending message for user  \"{}\":\"{}\". Data: {}. Exception: {}",
                            LABEL, actorPath(), userId(), sessionId(), data, ex.getMessage());

                    clientConnection.send(Output.of(new ServerErrors(List.of(ex.getMessage()))));

                    return failedFuture(ex);
                });

        return Behaviors.same();
    }

    private Behavior<ConnectionCommand> onSendWalkersPositionToClient(final SendWalkersPositionToClient command) {
        log.debug(logMessage("on send walkers position to client"));

        clientConnection.send(Output.of(command.toDomain()));

        return Behaviors.same();
    }

    private @NonNull String actorPath() {
        return getContext().getSelf().path().toString();
    }

    private @NonNull String actorId() {
        return getContext().getSelf().path().name();
    }

    private @Nullable String sessionId() {
        return Optional
                .ofNullable(clientConnection)
                .map(ClientConnection::getSessionId)
                .orElse(null);
    }

    private @Nullable String userId() {
        return Optional
                .ofNullable(clientConnection)
                .map(ClientConnection::getUserId)
                .orElse(null);
    }

    private boolean isSameConnection(final ClientConnection connection) {
        return Objects.equals(sessionId(), connection.getSessionId());
    }

    private String logMessage(final String message) {
        return "%s[%s] %s \"%s\":\"%s\"".formatted(LABEL, actorPath(), message, userId(), sessionId());
    }

}

package momomomo.dungeonwalker.wsserver.core.actor.connection;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.commons.DateTimeManager;
import momomomo.dungeonwalker.contract.client.ClientRequestProto.ClientRequest;
import momomomo.dungeonwalker.contract.engine.EngineMessageProto.EngineMessage;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.CloseConnection;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.ConnectionCommand;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.PollFromConsumer;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.SendHeartbeatToClient;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.SendMessageFromClient;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.SetConnection;
import momomomo.dungeonwalker.wsserver.core.config.properties.heartbeat.HeartbeatProps;
import momomomo.dungeonwalker.wsserver.core.handler.client.DataHandlerSelector;
import momomomo.dungeonwalker.wsserver.domain.handler.MessageHandlerSelector;
import momomomo.dungeonwalker.wsserver.domain.inbound.ClientConnection;
import momomomo.dungeonwalker.wsserver.domain.inbound.Consumer;
import momomomo.dungeonwalker.wsserver.domain.inbound.ConsumerFactory;
import momomomo.dungeonwalker.wsserver.domain.outbound.Sender;
import momomomo.dungeonwalker.wsserver.domain.output.Output;
import momomomo.dungeonwalker.wsserver.domain.output.ServerErrors;
import momomomo.dungeonwalker.wsserver.domain.output.ServerHeartbeat;
import momomomo.dungeonwalker.wsserver.domain.output.ServerMessage;
import org.apache.pekko.actor.typed.Behavior;
import org.apache.pekko.actor.typed.javadsl.AbstractBehavior;
import org.apache.pekko.actor.typed.javadsl.ActorContext;
import org.apache.pekko.actor.typed.javadsl.Behaviors;
import org.apache.pekko.actor.typed.javadsl.Receive;
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

    private final ConsumerFactory<EngineMessage> consumerFactory;
    private final MessageHandlerSelector<EngineMessage, ClientConnection, Void> messageHandlerSelector;
    private final DataHandlerSelector dataHandlerSelector;
    private final DateTimeManager dateTimeManager;
    private final HeartbeatProps heartbeatProps;
    private final Sender<ClientRequest> sender;

    private ClientConnection currentConnection;
    private Consumer<EngineMessage> consumer;

    private ConnectionActor(
            @NonNull final ActorContext<ConnectionCommand> context,
            @NonNull final ConsumerFactory<EngineMessage> consumerFactory,
            @NonNull final MessageHandlerSelector<EngineMessage, ClientConnection, Void> messageHandlerSelector,
            @NonNull final DataHandlerSelector dataHandlerSelector,
            @NonNull final DateTimeManager dateTimeManager,
            @NonNull final HeartbeatProps heartbeatProps,
            @NonNull final Sender<ClientRequest> sender
    ) {
        super(context);
        this.consumerFactory = consumerFactory;
        this.messageHandlerSelector = messageHandlerSelector;
        this.dataHandlerSelector = dataHandlerSelector;
        this.dateTimeManager = dateTimeManager;
        this.heartbeatProps = heartbeatProps;
        this.sender = sender;
    }

    public static Behavior<ConnectionCommand> create(
            @NonNull final ConsumerFactory<EngineMessage> consumerFactory,
            @NonNull final MessageHandlerSelector<EngineMessage, ClientConnection, Void> messageHandlerSelector,
            @NonNull final DataHandlerSelector dataHandlerSelector,
            @NonNull final DateTimeManager dateTimeManager,
            @NonNull final HeartbeatProps heartbeatProps,
            @NonNull final Sender<ClientRequest> sender
    ) {
        log.debug("{} create", LABEL);
        return Behaviors.setup(context ->
                new ConnectionActor(
                        context,
                        consumerFactory,
                        messageHandlerSelector,
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
                .onMessage(SendMessageFromClient.class, this::onSendMessageFromClient)
                .onMessage(PollFromConsumer.class, this::onPollFromConsumer)
                .build();
    }

    private Behavior<ConnectionCommand> onSetConnection(final SetConnection command) {
        log.debug("{}[{}] on set connection \"{}\":\"{}\"", LABEL,
                actorPath(),
                command.connection().getUserId(),
                command.connection().getSessionId());

        currentConnection = command.connection();
        consumer = consumerFactory.create(actorPath());
        consumer.start();

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

            final var timerConsumerPollKey = "timer-consumer-" + actorId();
            log.debug("{}[{}] setting timer \"{}\"", LABEL, actorPath(), timerConsumerPollKey);

            if (timer.isTimerActive(timerConsumerPollKey)) {
                timer.cancel(timerConsumerPollKey);
            }

            timer.startTimerWithFixedDelay(
                    timerConsumerPollKey,
                    new PollFromConsumer(command.connection()),
                    Duration.of(100, ChronoUnit.MILLIS));

            return connected();
        });
    }

    private Behavior<ConnectionCommand> onResetConnection(@NonNull final SetConnection command) {
        final var cmdUserId = command.connection().getUserId();
        final var cmdSessionId = command.connection().getSessionId();

        log.debug("{}[{}] on set connection again \"{}\":\"{}\" vs. \"{}\":\"{}\"",
                LABEL, actorPath(), userId(), sessionId(), cmdUserId, cmdSessionId);

        if (!Objects.equals(currentConnection.getSessionId(), cmdSessionId)) {
            log.warn("{}[{}] Resetting connection since sessions has changed \"{}\":\"{}\" vs. \"{}\":\"{}\"",
                    LABEL, actorPath(), userId(), sessionId(), cmdUserId, cmdSessionId);
            currentConnection.close();
            currentConnection = command.connection();

        } else {
            log.warn("{}[{}] Connection with same session ID will not be reset \"{}\":\"{}\"",
                    LABEL, actorPath(), userId(), sessionId());
        }

        return Behaviors.same();
    }

    private Behavior<ConnectionCommand> onCloseConnection(@NonNull final CloseConnection command) {
        final var cmdUserId = command.connection().getSessionId();

        log.debug("{}[{}] on close connection \"{}\":\"{}\"", LABEL, actorPath(), command.connection().getUserId(), cmdUserId);

        if (isSameConnection(command.connection())) {
            log.debug("{}[{}] closing connection \"{}\":\"{}\"", LABEL, actorPath(), command.connection().getUserId(), cmdUserId);
            return Behaviors.stopped();
        }

        return Behaviors.same();
    }

    private Behavior<ConnectionCommand> onSendHeartbeatToClient(@NonNull final SendHeartbeatToClient command) {
        log.debug("{}[{}] on send heartbeat to \"{}\":\"{}\"", LABEL, actorPath(), userId(), sessionId());

        final var output = Output.of(
                new ServerHeartbeat(
                        heartbeatProps.getDelay(),
                        heartbeatProps.getTimeUnit(),
                        dateTimeManager.instantNow()));

        currentConnection.send(output);

        return Behaviors.same();
    }

    private Behavior<ConnectionCommand> onSendMessageFromClient(@NonNull final SendMessageFromClient command) {
        log.debug("{}[{}] on act on message \"{}\":\"{}\"", LABEL, actorPath(), userId(), sessionId());

        dataHandlerSelector
                .select(command.message().data())
                .handle(command.message().data(), sender)
                .thenCompose(result -> {
                    switch (result.type()) {
                        case FAILURE -> {
                            log.error("{}[{}] Failure sending message for user \"{}\":\"{}\". Result: {}",
                                    LABEL, actorPath(), userId(), sessionId(), result);
                            currentConnection.send(Output.of(new ServerErrors(result.errors())));
                        }
                        case SUCCESS -> {
                            log.debug("{}[{}] Success sending message for user \"{}\":\"{}\"",
                                    LABEL, actorPath(), userId(), sessionId());
                            currentConnection.send(Output.of(new ServerMessage("Message sent")));
                        }
                        default -> {
                            log.debug("{}[{}] Ignoring message for user \"{}\":\"{}\"",
                                    LABEL, actorPath(), userId(), sessionId());
                            currentConnection.send(Output.of(new ServerMessage("Message ignored")));
                        }
                    }

                    return CompletableFuture.completedFuture(null);

                }).exceptionally(ex -> {
                    log.error("{}[{}] Error sending message for user  \"{}\":\"{}\". Exception: {}",
                            LABEL, actorPath(), userId(), sessionId(), ex.getMessage());

                    currentConnection.send(Output.of(new ServerErrors(List.of(ex.getMessage()))));

                    return failedFuture(ex);
                });

        return Behaviors.same();
    }

    private Behavior<ConnectionCommand> onPollFromConsumer(@NonNull final PollFromConsumer command) {
        consumer
                .poll()
                .forEach(message ->
                        messageHandlerSelector
                                .select(message)
                                .handle(message, currentConnection));

        return Behaviors.same();
    }

    private @Nonnull String actorPath() {
        return getContext().getSelf().path().toString();
    }

    private @Nonnull String actorId() {
        return getContext().getSelf().path().name();
    }

    private @Nullable String sessionId() {
        return Optional
                .ofNullable(currentConnection)
                .map(ClientConnection::getSessionId)
                .orElse(null);
    }

    private @Nullable String userId() {
        return Optional
                .ofNullable(currentConnection)
                .map(ClientConnection::getUserId)
                .orElse(null);
    }

    private boolean isSameConnection(final ClientConnection connection) {
        return Objects.equals(sessionId(), connection.getSessionId());
    }

}

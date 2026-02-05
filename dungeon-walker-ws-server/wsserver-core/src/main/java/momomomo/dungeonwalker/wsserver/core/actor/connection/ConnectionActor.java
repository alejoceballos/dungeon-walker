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
import momomomo.dungeonwalker.wsserver.core.config.HeartbeatConfig;
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

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class ConnectionActor extends AbstractBehavior<ConnectionCommand> {

    private final ConsumerFactory<EngineMessage> consumerFactory;
    private final MessageHandlerSelector<EngineMessage, ClientConnection, Void> messageHandlerSelector;
    private final DataHandlerSelector dataHandlerSelector;
    private final DateTimeManager dateTimeManager;
    private final HeartbeatConfig heartbeatConfig;
    private final Sender<ClientRequest> sender;

    private ClientConnection currentConnection;
    private Consumer<EngineMessage> consumer;

    private ConnectionActor(
            @NonNull final ActorContext<ConnectionCommand> context,
            @NonNull final ConsumerFactory<EngineMessage> consumerFactory,
            @NonNull final MessageHandlerSelector<EngineMessage, ClientConnection, Void> messageHandlerSelector,
            @NonNull final DataHandlerSelector dataHandlerSelector,
            @NonNull final DateTimeManager dateTimeManager,
            @NonNull final HeartbeatConfig heartbeatConfig,
            @NonNull final Sender<ClientRequest> sender
    ) {
        super(context);
        this.consumerFactory = consumerFactory;
        this.messageHandlerSelector = messageHandlerSelector;
        this.dataHandlerSelector = dataHandlerSelector;
        this.dateTimeManager = dateTimeManager;
        this.heartbeatConfig = heartbeatConfig;
        this.sender = sender;
    }

    public static Behavior<ConnectionCommand> create(
            @NonNull final ConsumerFactory<EngineMessage> consumerFactory,
            @NonNull final MessageHandlerSelector<EngineMessage, ClientConnection, Void> messageHandlerSelector,
            @NonNull final DataHandlerSelector dataHandlerSelector,
            @NonNull final DateTimeManager dateTimeManager,
            @NonNull final HeartbeatConfig heartbeatConfig,
            @NonNull final Sender<ClientRequest> sender
    ) {
        log.debug("---> [ACTOR - Connection] create");
        return Behaviors.setup(context ->
                new ConnectionActor(
                        context,
                        consumerFactory,
                        messageHandlerSelector,
                        dataHandlerSelector,
                        dateTimeManager,
                        heartbeatConfig,
                        sender));
    }

    @Override
    public Receive<ConnectionCommand> createReceive() {
        log.debug("---> [ACTOR - Connection][{}] create receive", actorId());
        return newReceiveBuilder()
                .onMessage(SetConnection.class, this::onSetConnection)
                .build();
    }

    public Receive<ConnectionCommand> connected() {
        log.debug("---> [ACTOR - Connection][{}] session connection", actorId());
        return newReceiveBuilder()
                .onMessage(SetConnection.class, this::onResetConnection)
                .onMessage(CloseConnection.class, this::onCloseConnection)
                .onMessage(SendHeartbeatToClient.class, this::onSendHeartbeatToClient)
                .onMessage(SendMessageFromClient.class, this::onSendMessageFromClient)
                .onMessage(PollFromConsumer.class, this::onPollFromConsumer)
                .build();
    }

    public Behavior<ConnectionCommand> connectionClosed() {
        log.debug("---> [ACTOR - Connection][{}] connection closed", actorId());
        return Behaviors.stopped();
    }

    private Behavior<ConnectionCommand> onSetConnection(final SetConnection command) {
        log.debug("---> [ACTOR - Connection][{}] on set connection \"{}\":\"{}\"",
                actorPath(),
                command.connection().getUserId(),
                command.connection().getSessionId());

        currentConnection = command.connection();
        consumer = consumerFactory.create(actorPath());
        consumer.start();

        return Behaviors.withTimers(timer -> {
            final var timerHeartbeatKey = "timer-heartbeat-" + actorId();
            log.debug("---> [ACTOR - Connection][{}] setting timer \"{}\"", actorPath(), timerHeartbeatKey);

            if (timer.isTimerActive(timerHeartbeatKey)) {
                timer.cancel(timerHeartbeatKey);
            }

            timer.startTimerWithFixedDelay(
                    timerHeartbeatKey,
                    new SendHeartbeatToClient(),
                    Duration.of(
                            heartbeatConfig.getDelay(),
                            ChronoUnit.valueOf(heartbeatConfig.getTimeUnit())));

            final var timerConsumerPollKey = "timer-consumer-" + actorId();
            log.debug("---> [ACTOR - Connection][{}] setting timer \"{}\"", actorPath(), timerConsumerPollKey);

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
        log.debug("---> [ACTOR - Connection][{}] on set connection again \"{}\":\"{}\" vs. \"{}\":\"{}\"",
                actorPath(), userId(), sessionId(), command.connection().getUserId(), command.connection().getSessionId());

        if (!Objects.equals(currentConnection.getSessionId(), command.connection().getSessionId())) {
            log.warn("---> [ACTOR - Connection][{}] Resetting connection since sessions has changed \"{}\":\"{}\" vs. \"{}\":\"{}\"",
                    actorPath(), userId(), sessionId(), command.connection().getUserId(), command.connection().getSessionId());
            currentConnection.close();
            currentConnection = command.connection();

        } else {
            log.warn("---> [ACTOR - Connection][{}] Connection with same session ID will not be reset \"{}\":\"{}\"",
                    actorPath(), userId(), sessionId());
        }

        return Behaviors.same();
    }

    private Behavior<ConnectionCommand> onCloseConnection(@NonNull final CloseConnection command) {
        log.debug("---> [ACTOR - Connection][{}] on close connection \"{}\":\"{}\"",
                actorPath(), command.connection().getUserId(), command.connection().getSessionId());

        if (isSameConnection(command.connection())) {
            log.debug("---> [ACTOR - Connection][{}] closing connection \"{}\":\"{}\"",
                    actorPath(), command.connection().getUserId(), command.connection().getSessionId());
            return connectionClosed();
        }

        return Behaviors.same();
    }

    private Behavior<ConnectionCommand> onSendHeartbeatToClient(@NonNull final SendHeartbeatToClient command) {
        log.debug("---> [ACTOR - Connection][{}] on send heartbeat to \"{}\":\"{}\"",
                actorPath(), currentConnection.getUserId(), currentConnection.getSessionId());

        final var output = Output.of(
                new ServerHeartbeat(
                        heartbeatConfig.getDelay(),
                        heartbeatConfig.getTimeUnit(),
                        dateTimeManager.instantNow()));

        currentConnection.send(output);

        return Behaviors.same();
    }

    private Behavior<ConnectionCommand> onSendMessageFromClient(@NonNull final SendMessageFromClient command) {
        log.debug("---> [ACTOR - Connection][{}] on act on message \"{}\":\"{}\"",
                actorPath(), currentConnection.getUserId(), currentConnection.getSessionId());

        dataHandlerSelector
                .select(command.message().data())
                .handle(command.message().data(), sender)
                .thenCompose(result -> {
                    switch (result.type()) {
                        case FAILURE -> {
                            log.error("---> [ACTOR - Connection][{}] Failure sending message for user \"{}\":\"{}\". Result: {}",
                                    actorPath(), currentConnection.getUserId(), currentConnection.getSessionId(), result);
                            currentConnection.send(Output.of(new ServerErrors(result.errors())));
                        }
                        case SUCCESS -> {
                            log.debug("---> [ACTOR - Connection][{}] Success sending message for user \"{}\":\"{}\"",
                                    actorPath(), currentConnection.getUserId(), currentConnection.getSessionId());
                            currentConnection.send(Output.of(new ServerMessage("Message sent")));
                        }
                        default -> {
                            log.debug("---> [ACTOR - Connection][{}] Ignoring message for user \"{}\":\"{}\"",
                                    actorPath(), currentConnection.getUserId(), currentConnection.getSessionId());
                            currentConnection.send(Output.of(new ServerMessage("Message ignored")));
                        }
                    }

                    return CompletableFuture.completedFuture(null);

                }).exceptionally(ex -> {
                    log.error("---> [ACTOR - Connection][{}] Error sending message for user  \"{}\":\"{}\". Exception: {}",
                            actorPath(), currentConnection.getUserId(), currentConnection.getSessionId(), ex.getMessage());

                    currentConnection.send(Output.of(new ServerErrors(List.of(ex.getMessage()))));

                    return CompletableFuture.failedFuture(ex);
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

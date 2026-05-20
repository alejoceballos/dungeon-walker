package momomomo.dungeonwalker.wsserver.core.actor.connection;

import jakarta.annotation.Nullable;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.commons.DateTimeManager;
import momomomo.dungeonwalker.commons.conditional.Conditional;
import momomomo.dungeonwalker.contract.client.ClientRequestProto.ClientRequest;
import momomomo.dungeonwalker.contract.client.DirectionProto.Direction;
import momomomo.dungeonwalker.contract.client.EnterDungeonProto.EnterDungeon;
import momomomo.dungeonwalker.contract.client.LeaveDungeonProto.LeaveDungeon;
import momomomo.dungeonwalker.contract.client.MovementProto.Movement;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.CloseConnection;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.ConnectionCommand;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.SendHeartbeatToClient;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.SetConnection;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.from.client.AuthenticateFromClient;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.from.client.MovementFromClient;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.from.engine.BroadcastDungeonState;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.from.engine.EnteredTheDungeon;
import momomomo.dungeonwalker.wsserver.core.config.properties.heartbeat.HeartbeatProps;
import momomomo.dungeonwalker.wsserver.domain.auth.Authorizer;
import momomomo.dungeonwalker.wsserver.domain.data.client.output.Output;
import momomomo.dungeonwalker.wsserver.domain.data.client.output.ServerErrors;
import momomomo.dungeonwalker.wsserver.domain.data.client.output.ServerHeartbeat;
import momomomo.dungeonwalker.wsserver.domain.data.client.output.ServerMessage;
import momomomo.dungeonwalker.wsserver.domain.data.engine.output.EngineOutbound;
import momomomo.dungeonwalker.wsserver.domain.outbound.ClientOutbound;
import org.apache.commons.lang3.StringUtils;
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

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
public class ConnectionActor extends AbstractBehavior<ConnectionCommand> {

    private static final String LABEL = "---> [ACTOR - Connection]";

    public static final EntityTypeKey<ConnectionCommand> ENTITY_TYPE_KEY =
            EntityTypeKey.create(ConnectionCommand.class, "connection-actor-type-key");

    private final DateTimeManager dateTimeManager;
    private final HeartbeatProps heartbeatProps;
    private final Authorizer authorizer;
    private final EngineOutbound<ClientRequest> engineOutbound;

    private ClientOutbound clientOutbound;
    private String clientId;

    private ConnectionActor(
            @NonNull final ActorContext<ConnectionCommand> context,
            @NonNull final ActorRef<Topic.Command<ConnectionCommand>> connectionBroadcastTopic,
            @NonNull final DateTimeManager dateTimeManager,
            @NonNull final HeartbeatProps heartbeatProps,
            @NonNull final Authorizer authorizer,
            @NonNull final EngineOutbound<ClientRequest> engineOutbound
    ) {
        super(context);
        this.dateTimeManager = dateTimeManager;
        this.heartbeatProps = heartbeatProps;
        this.authorizer = authorizer;
        this.engineOutbound = engineOutbound;

        connectionBroadcastTopic.tell(Topic.subscribe(context.getSelf()));
    }

    public static Behavior<ConnectionCommand> create(
            @NonNull final ActorRef<Topic.Command<ConnectionCommand>> connectionBroadcastTopic,
            @NonNull final DateTimeManager dateTimeManager,
            @NonNull final HeartbeatProps heartbeatProps,
            @NonNull final Authorizer authorizer,
            @NonNull final EngineOutbound<ClientRequest> sender
    ) {
        log.debug("{} create", LABEL);
        return Behaviors.setup(context ->
                new ConnectionActor(
                        context,
                        connectionBroadcastTopic,
                        dateTimeManager,
                        heartbeatProps,
                        authorizer,
                        sender));
    }

    @Override
    public Receive<ConnectionCommand> createReceive() {
        log.debug(logShortMessage("Initial (Create/Receive) state"));
        return newReceiveBuilder()
                .onMessage(SetConnection.class, this::onSetConnection)
                .build();
    }

    public Receive<ConnectionCommand> connected() {
        log.debug(logShortMessage("Connected state"));
        return newReceiveBuilder()
                .onMessage(CloseConnection.class, this::onCloseConnection)
                .onMessage(AuthenticateFromClient.class, this::onAuthenticateFromClient)
                .build();
    }

    public Receive<ConnectionCommand> authenticated() {
        log.debug(logFullMessage("Authenticated state"));
        return newReceiveBuilder()
                .onMessage(CloseConnection.class, this::onCloseConnection)
                .onMessage(SendHeartbeatToClient.class, this::onSendHeartbeatToClient)
                .onMessage(EnteredTheDungeon.class, this::onEnteredTheDungeon)
                .build();
    }

    public Receive<ConnectionCommand> inPLay() {
        log.debug(logFullMessage("In-play state"));
        return newReceiveBuilder()
                .onMessage(CloseConnection.class, this::onCloseConnection)
                .onMessage(SendHeartbeatToClient.class, this::onSendHeartbeatToClient)
                .onMessage(BroadcastDungeonState.class, this::onBroadcastDungeonState)
                .onMessage(MovementFromClient.class, this::onMovementFromClient)
                .build();
    }

    private Behavior<ConnectionCommand> onSetConnection(final SetConnection command) {
        log.debug(logShortMessage("On set connection"));
        clientOutbound = command.connection();
        clientOutbound.send(Output.of(new ServerMessage("Connected")));
        return connected();
    }

    private Behavior<ConnectionCommand> onCloseConnection(final CloseConnection command) {
        log.debug(logFullMessage("on close connection"));

        return Behaviors.withTimers(timer -> {
            final var timerHeartbeatKey = "timer-heartbeat-" + logActorId();

            if (timer.isTimerActive(timerHeartbeatKey)) {
                log.debug(logFullMessage("cancelling active {} timer due to connection close"), timerHeartbeatKey);
                timer.cancel(timerHeartbeatKey);

            } else {
                log.debug(logFullMessage("{} timer not active. Continuing on closing connection"), timerHeartbeatKey);
            }

            if (nonNull(clientId)) {
                engineOutbound.send(ClientRequest
                        .newBuilder()
                        .setClientId(clientId)
                        .setLeaveDungeon(LeaveDungeon
                                .newBuilder()
                                .build())
                        .build());
            }

            return Behaviors.stopped();
        });
    }

    private Behavior<ConnectionCommand> onAuthenticateFromClient(final AuthenticateFromClient command) {
        log.debug(logShortMessage("on authenticate"));

        clientId = authorizer.authorize(command.credentials());

        if (isBlank(clientId)) {
            log.debug(logShortMessage("Failed to authorize"));
            tellSelfTo(new CloseConnection());
            return Behaviors.same();
        }

        /*
         * TODO: Security flaw!
         *  If another actor is logged with the same client id, meaning it has connected to the WebSocket from two
         *  different clients, the process must be as follows:
         *    1. Remove the client from the game (send "leave" to the engine with the current client id)
         *    2. Log it out (from the other actor, send a "session.close")
         *    3. Only then, set the timers below and so on
         *  That's a security flow that must be dealt separately, synchronously.
         */

        return Behaviors.withTimers(timer -> {
            final var timerHeartbeatKey = "timer-heartbeat-" + clientId;
            log.debug("{}[{}] setting timer \"{}\"", LABEL, clientId, timerHeartbeatKey);

            if (timer.isTimerActive(timerHeartbeatKey)) {
                timer.cancel(timerHeartbeatKey);
            }

            timer.startTimerWithFixedDelay(
                    timerHeartbeatKey,
                    new SendHeartbeatToClient(),
                    Duration.of(
                            heartbeatProps.getDelay(),
                            ChronoUnit.valueOf(heartbeatProps.getTimeUnit())));

            engineOutbound
                    .send(ClientRequest
                            .newBuilder()
                            .setClientId(clientId)
                            .setEnterDungeon(EnterDungeon.newBuilder().build())
                            .build())
                    .thenAccept(_ -> clientOutbound
                            .send(Output.of(new ServerMessage("Authentication successful. Entering the dungeon"))))
                    .exceptionally(_ -> {
                        clientOutbound
                                .send(Output.of(new ServerErrors(
                                        List.of("Authentication successful, but failed to enter the dungeon"))));
                        return null;
                    });

            return authenticated();
        });
    }

    private Behavior<ConnectionCommand> onSendHeartbeatToClient(final SendHeartbeatToClient command) {
        log.debug(logFullMessage("on send heartbeat to"));

        final var output = Output.of(
                new ServerHeartbeat(
                        heartbeatProps.getDelay(),
                        heartbeatProps.getTimeUnit(),
                        dateTimeManager.instantNow()));

        clientOutbound.send(output);

        return Behaviors.same();
    }

    private Behavior<ConnectionCommand> onEnteredTheDungeon(final EnteredTheDungeon command) {
        log.debug(logFullMessage("on entered the dungeon"));

        if (!command.clientId().equals(clientId)) {
            log.debug(
                    logFullMessage("[on entered the dungeon] Client {} in command does not match actor's client {}. Ignoring command."),
                    command.clientId(),
                    clientId);
            return Behaviors.same();
        }

        clientOutbound.send(Output.of(command.toDomain()));

        return inPLay();
    }

    private Behavior<ConnectionCommand> onBroadcastDungeonState(final BroadcastDungeonState command) {
        log.debug(logFullMessage("on send walkers position to client"));

        clientOutbound.send(Output.of(command.toDomain()));

        return Behaviors.same();
    }

    private Behavior<ConnectionCommand> onMovementFromClient(final MovementFromClient command) {
        log.debug(logFullMessage("on movement from client"));

        final String direction = command.direction().name();

        engineOutbound
                .send(ClientRequest
                        .newBuilder()
                        .setClientId(clientId)
                        .setMovement(Movement
                                .newBuilder()
                                .setDirection(Direction.valueOf(direction))
                                .build())
                        .build())
                .thenAccept(_ -> clientOutbound
                        .send(Output.of(new ServerMessage("Movements to \"%s\" sent".formatted(direction)))))
                .exceptionally(_ -> {
                    clientOutbound
                            .send(Output.of(new ServerErrors(
                                    List.of("Could not send movement to \"%s\"".formatted(direction)))));
                    return null;
                });

        return Behaviors.same();
    }

    private void tellSelfTo(@NonNull final ConnectionCommand command) {
        getContext().getSelf().tell(command);
    }

    private @NonNull String logActorPath() {
        return getContext().getSelf().path().toString();
    }

    private @NonNull String logActorId() {
        return getContext().getSelf().path().name();
    }

    private @Nullable String logClientId() {
        return Conditional
                .on(() -> StringUtils.isNotBlank(clientId))
                .thenGet(() -> clientId)
                .orElseGet(() -> "** unavailable **")
                .evaluate();
    }

    private String logShortMessage(final String message) {
        return "%s[%s]: %s".formatted(LABEL, logActorId(), message);
    }

    private String logFullMessage(final String message) {
        // ---> [ACTOR - Connection][akka://system/user/sharding/connection-actor-type-key/123][some-user-name]: message
        return "%s[%s][%s]: %s".formatted(LABEL, logActorPath(), logClientId(), message);
    }

}

package momomomo.dungeonwalker.wsserver.core.actor.client;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.commons.conditional.Conditional;
import momomomo.dungeonwalker.contract.client.ClientHeartbeatProto;
import momomomo.dungeonwalker.contract.client.ClientRequestProto.ClientRequest;
import momomomo.dungeonwalker.contract.client.DirectionProto.Direction;
import momomomo.dungeonwalker.contract.client.EnterDungeonProto.EnterDungeon;
import momomomo.dungeonwalker.contract.client.LeaveDungeonProto.LeaveDungeon;
import momomomo.dungeonwalker.contract.client.MovementProto.Movement;
import momomomo.dungeonwalker.wsserver.core.actor.client.command.ClientCommand;
import momomomo.dungeonwalker.wsserver.core.actor.client.command.from.connection.ConnectionAuthenticatedCommand;
import momomomo.dungeonwalker.wsserver.core.actor.client.command.from.connection.ConnectionCloseCommand;
import momomomo.dungeonwalker.wsserver.core.actor.client.command.from.connection.ConnectionHeartbeatCommand;
import momomomo.dungeonwalker.wsserver.core.actor.client.command.from.connection.MoveCommand;
import momomomo.dungeonwalker.wsserver.core.actor.client.command.from.engine.DungeonCellStateChangedCommand;
import momomomo.dungeonwalker.wsserver.core.actor.client.command.from.engine.DungeonStateChangedCommand;
import momomomo.dungeonwalker.wsserver.core.actor.client.command.from.engine.EngineHeartbeatCommand;
import momomomo.dungeonwalker.wsserver.core.actor.connection.ConnectionActor;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.ConnectionCommand;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.from.client.ClientDungeonCellStateChangedCommand;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.from.client.ClientDungeonStateChangedCommand;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.from.client.ClientErrorMessageCommand;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.from.client.ClientHeartbeatCommand;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.from.client.ClientMessageCommand;
import momomomo.dungeonwalker.wsserver.domain.data.engine.output.EngineOutbound;
import org.apache.pekko.actor.typed.Behavior;
import org.apache.pekko.actor.typed.PostStop;
import org.apache.pekko.actor.typed.javadsl.AbstractBehavior;
import org.apache.pekko.actor.typed.javadsl.ActorContext;
import org.apache.pekko.actor.typed.javadsl.Behaviors;
import org.apache.pekko.actor.typed.javadsl.Receive;
import org.apache.pekko.cluster.sharding.typed.javadsl.ClusterSharding;
import org.apache.pekko.cluster.sharding.typed.javadsl.EntityTypeKey;

import static momomomo.dungeonwalker.wsserver.core.actor.client.ClientActor.STATE.AUTHENTICATED;
import static momomomo.dungeonwalker.wsserver.core.actor.client.ClientActor.STATE.INITIALIZED;
import static momomomo.dungeonwalker.wsserver.core.actor.client.ClientActor.STATE.IN_PLAY;
import static momomomo.dungeonwalker.wsserver.core.actor.client.ClientActor.STATE.UNINITIALIZED;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
public class ClientActor extends AbstractBehavior<ClientCommand> {

    @RequiredArgsConstructor
    enum STATE {
        UNINITIALIZED("Uninitialized"),
        INITIALIZED("Initialized"),
        AUTHENTICATED("Authenticated"),
        IN_PLAY("In Play");

        @Getter
        private final String value;
    }

    private static final String LABEL = "---> [ACTOR - Client]";

    public static final EntityTypeKey<ClientCommand> ENTITY_TYPE_KEY =
            EntityTypeKey.create(ClientCommand.class, ClientActor.class.getSimpleName() + "-type-key");

    private final ClusterSharding clusterSharding;
    private final EngineOutbound<ClientRequest> engineOutbound;

    private String connectionId;
    private STATE state;

    private ClientActor(
            @NonNull final ActorContext<ClientCommand> context,
            @NonNull final ClusterSharding clusterSharding,
            @NonNull final EngineOutbound<ClientRequest> engineOutbound
    ) {
        super(context);
        this.clusterSharding = clusterSharding;
        this.engineOutbound = engineOutbound;
        this.state = UNINITIALIZED;
    }

    public static Behavior<ClientCommand> create(
            @NonNull final ClusterSharding clusterSharding,
            @NonNull final EngineOutbound<ClientRequest> sender
    ) {
        log.debug("{} create", LABEL);
        return Behaviors.setup(context ->
                new ClientActor(
                        context,
                        clusterSharding,
                        sender));
    }

    @Override
    public Receive<ClientCommand> createReceive() {
        log.debug(logShortMessage("Initial (Create/Receive) state"));

        state = INITIALIZED;

        return newReceiveBuilder()
                .onMessage(ConnectionAuthenticatedCommand.class, this::onConnectionAuthenticated)
                .onAnyMessage(command -> {
                    log.warn(logShortMessage("Received unexpected command while in initial state: {}"), command);
                    return Behaviors.same();
                })
                .onSignal(PostStop.class, this::onPostStop)
                .build();
    }

    public Receive<ClientCommand> connectionAuthenticated() {
        log.debug(logShortMessage("Connection authenticated state"));

        state = AUTHENTICATED;

        return newReceiveBuilder()
                .onMessage(ConnectionCloseCommand.class, _ -> onConnectionClose())
                .onMessage(DungeonStateChangedCommand.class, this::onDungeonStateFirstChanged)
                .onAnyMessage(command -> {
                    log.warn(logShortMessage("Received unexpected command while in awake state: {}"), command);
                    return Behaviors.same();
                })
                .onSignal(PostStop.class, this::onPostStop)
                .build();
    }

    public Receive<ClientCommand> inPLay() {
        log.debug(logFullMessage("In-play state"));

        state = IN_PLAY;

        return newReceiveBuilder()
                .onMessage(ConnectionCloseCommand.class, _ -> onConnectionClose())
                .onMessage(DungeonCellStateChangedCommand.class, this::onDungeonCellStateChanged)
                .onMessage(DungeonStateChangedCommand.class, this::onDungeonStateChanged)
                .onMessage(MoveCommand.class, this::onMove)
                .onMessage(ConnectionHeartbeatCommand.class, this::onConnectionHeartbeat)
                .onMessage(EngineHeartbeatCommand.class, this::onEngineHeartbeat)
                .onAnyMessage(command -> {
                    log.warn(logShortMessage("Received unexpected command while in in-play state: {}"), command);
                    return Behaviors.same();
                })
                .onSignal(PostStop.class, this::onPostStop)
                .build();
    }

    private Behavior<ClientCommand> onConnectionAuthenticated(
            @NonNull final ConnectionAuthenticatedCommand command
    ) {
        log.debug(logShortMessage("on connection authenticated"));

        connectionId = command.connectionId();

        engineOutbound
                .send(ClientRequest
                        .newBuilder()
                        .setClientId(actorId())
                        .setEnterDungeon(EnterDungeon.newBuilder().build())
                        .build())
                .thenAccept(_ -> tellConnection(new ClientMessageCommand("Entering the dungeon. Wait")))
                .exceptionally(ex -> tellConnection(new ClientErrorMessageCommand("Error entering the dungeon", ex)));

        return connectionAuthenticated();
    }

    private Behavior<ClientCommand> onPostStop(@NonNull final PostStop signal) {
        log.debug(logFullMessage("on post stop: {}"), signal);
        return Behaviors.same();
    }

    private Behavior<ClientCommand> onConnectionClose() {
        log.debug(logFullMessage("on connection close"));

        engineOutbound
                .send(ClientRequest
                        .newBuilder()
                        .setClientId(actorId())
                        .setLeaveDungeon(LeaveDungeon.newBuilder().build())
                        .build())
                .thenAccept(_ -> log.debug(logFullMessage("Successfully sent \"leave dungeon\" request to engine")))
                .exceptionally(ex -> {
                    log.error(logFullMessage("Error sending \"leave dungeon\" request to engine"), ex);
                    return null;
                });

        return Behaviors.stopped();
    }

    private Behavior<ClientCommand> onDungeonStateFirstChanged(@NonNull final DungeonStateChangedCommand command) {
        log.debug(logFullMessage("on dungeon state first changed"));

        if (!command.dungeonState().containsKey(actorId())) {
            return Behaviors.same();
        }

        tellConnection(ClientDungeonStateChangedCommand.of(command));

        return inPLay();
    }

    private Behavior<ClientCommand> onDungeonCellStateChanged(@NonNull final DungeonCellStateChangedCommand command) {
        log.debug(logFullMessage("on dungeon cell state changed"));

        tellConnection(ClientDungeonCellStateChangedCommand.of(command));

        return Behaviors.same();
    }

    private Behavior<ClientCommand> onDungeonStateChanged(@NonNull final DungeonStateChangedCommand command) {
        log.debug(logFullMessage("on dungeon state changed"));

        tellConnection(ClientDungeonStateChangedCommand.of(command));

        return Behaviors.same();
    }

    private Behavior<ClientCommand> onMove(@NonNull final MoveCommand command) {
        log.debug(logFullMessage("on move"));

        final String direction = command.direction().name();

        engineOutbound
                .send(ClientRequest
                        .newBuilder()
                        .setClientId(actorId())
                        .setMovement(Movement
                                .newBuilder()
                                .setDirection(Direction.valueOf(direction))
                                .build())
                        .build())
                .thenAccept(_ -> tellConnection(new ClientMessageCommand("\"Move %s\" message sent. Wait".formatted(direction))))
                .exceptionally(ex -> tellConnection(new ClientErrorMessageCommand("Error sending \"move %s\" message".formatted(direction), ex)));

        return Behaviors.same();
    }

    private Behavior<ClientCommand> onConnectionHeartbeat(@NonNull final ConnectionHeartbeatCommand command) {
        log.trace(logFullMessage("on connection heartbeat"));

        engineOutbound
                .send(ClientRequest
                        .newBuilder()
                        .setClientId(actorId())
                        .setHeartbeat(ClientHeartbeatProto.ClientHeartbeat.newBuilder().build())
                        .build())
                .thenAccept(_ -> log.trace(logFullMessage("Successfully sent heartbeat to engine")))
                .exceptionally(ex -> {
                    log.error(logFullMessage("Error sending heartbeat to engine"), ex);
                    return null;
                });

        return Behaviors.same();
    }

    private Behavior<ClientCommand> onEngineHeartbeat(@NonNull final EngineHeartbeatCommand command) {
        log.trace(logFullMessage("on engine heartbeat"));

        tellConnection(new ClientHeartbeatCommand());

        return Behaviors.same();
    }

    private Void tellConnection(@NonNull final ConnectionCommand command) {
        clusterSharding.entityRefFor(ConnectionActor.ENTITY_TYPE_KEY, connectionId).tell(command);
        return null;
    }

    private @NonNull String actorId() {
        return getContext().getSelf().path().name();
    }


    private @Nullable String logConnectionId() {
        return Conditional
                .on(() -> isNotBlank(connectionId))
                .thenGet(() -> connectionId)
                .orElseGet(() -> "** unavailable **")
                .evaluate();
    }

    private @NonNull String logShortMessage(@NonNull final String message) {
        return "%s[state: %s][actor: %s]: %s".formatted(LABEL, state.getValue(), actorId(), message);
    }

    private @NonNull String logFullMessage(@NonNull final String message) {
        return "%s[state: %s][actor: %s][connection: %s]: %s"
                .formatted(LABEL, state.getValue(), actorId(), logConnectionId(), message);
    }

}

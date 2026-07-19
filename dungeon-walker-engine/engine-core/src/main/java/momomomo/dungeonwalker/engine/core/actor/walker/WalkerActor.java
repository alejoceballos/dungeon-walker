package momomomo.dungeonwalker.engine.core.actor.walker;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.contract.engine.EngineHeartbeatProto.EngineHeartbeat;
import momomomo.dungeonwalker.contract.engine.EngineMessageProto.EngineMessage;
import momomomo.dungeonwalker.engine.core.actor.dungeon.DungeonActor;
import momomomo.dungeonwalker.engine.core.actor.dungeon.command.DungeonCommand;
import momomomo.dungeonwalker.engine.core.actor.dungeon.command.from.walker.MoveWalker;
import momomomo.dungeonwalker.engine.core.actor.dungeon.command.from.walker.PlaceWalker;
import momomomo.dungeonwalker.engine.core.actor.dungeon.command.from.walker.RemoveWalker;
import momomomo.dungeonwalker.engine.core.actor.walker.command.WalkerCommand;
import momomomo.dungeonwalker.engine.core.actor.walker.command.from.client.Leave;
import momomomo.dungeonwalker.engine.core.actor.walker.command.from.client.Move;
import momomomo.dungeonwalker.engine.core.actor.walker.command.from.client.UserHeartbeat;
import momomomo.dungeonwalker.engine.core.actor.walker.command.from.client.WakeUp;
import momomomo.dungeonwalker.engine.core.actor.walker.command.from.dungeon.DungeonHeartbeat;
import momomomo.dungeonwalker.engine.core.actor.walker.command.from.dungeon.Stop;
import momomomo.dungeonwalker.engine.core.actor.walker.command.from.dungeon.UpdateCellState;
import momomomo.dungeonwalker.engine.core.actor.walker.command.from.dungeon.UpdateDungeonState;
import momomomo.dungeonwalker.engine.core.setup.DungeonIdentity;
import momomomo.dungeonwalker.engine.domain.model.coordinates.CoordinatesManager;
import momomomo.dungeonwalker.engine.domain.model.walker.state.Asleep;
import momomomo.dungeonwalker.engine.domain.model.walker.state.Awake;
import momomomo.dungeonwalker.engine.domain.model.walker.state.Moving;
import momomomo.dungeonwalker.engine.domain.model.walker.state.Stopped;
import momomomo.dungeonwalker.engine.domain.model.walker.state.WalkerState;
import momomomo.dungeonwalker.engine.domain.outbound.ClientOutbound;
import org.apache.pekko.actor.typed.ActorRef;
import org.apache.pekko.actor.typed.Behavior;
import org.apache.pekko.actor.typed.PostStop;
import org.apache.pekko.actor.typed.javadsl.ActorContext;
import org.apache.pekko.actor.typed.javadsl.Behaviors;
import org.apache.pekko.actor.typed.pubsub.Topic;
import org.apache.pekko.cluster.sharding.typed.javadsl.ClusterSharding;
import org.apache.pekko.cluster.sharding.typed.javadsl.EntityTypeKey;
import org.apache.pekko.persistence.typed.PersistenceId;
import org.apache.pekko.persistence.typed.state.javadsl.CommandHandler;
import org.apache.pekko.persistence.typed.state.javadsl.DurableStateBehavior;
import org.apache.pekko.persistence.typed.state.javadsl.Effect;
import org.apache.pekko.persistence.typed.state.javadsl.SignalHandler;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.isEmpty;

@Slf4j
public class WalkerActor extends DurableStateBehavior<WalkerCommand, WalkerState> {

    private static final String LABEL = "---> [ACTOR - Walker]";

    public static final EntityTypeKey<WalkerCommand> ENTITY_TYPE_KEY =
            EntityTypeKey.create(WalkerCommand.class, WalkerActor.class.getSimpleName() + "-type-key");

    private final ActorContext<WalkerCommand> context;
    private final ActorRef<Topic.Command<WalkerCommand>> walkerBroadcastTopic;
    private final DungeonIdentity dungeonIdentity;
    private final ClientOutbound<EngineMessage> clientOutbound;

    private WalkerActor(
            @NonNull final ActorContext<WalkerCommand> context,
            @NonNull final ActorRef<Topic.Command<WalkerCommand>> walkerBroadcastTopic,
            @NonNull final PersistenceId persistenceId,
            @NonNull final DungeonIdentity dungeonIdentity,
            @NonNull final ClientOutbound<EngineMessage> clientOutbound
    ) {
        this.context = context;
        this.walkerBroadcastTopic = walkerBroadcastTopic;
        this.dungeonIdentity = dungeonIdentity;
        this.clientOutbound = clientOutbound;

        super(persistenceId);

        this.walkerBroadcastTopic.tell(Topic.subscribe(context.getSelf()));
    }

    public static Behavior<WalkerCommand> create(
            @NonNull final ActorRef<Topic.Command<WalkerCommand>> walkerBroadcastTopic,
            @NonNull final PersistenceId persistenceId,
            @NonNull final DungeonIdentity dungeonIdentity,
            @NonNull final ClientOutbound<EngineMessage> clientOutbound
    ) {
        log.debug("{}[persistenceId: {}] create", LABEL, persistenceId);
        return Behaviors.setup(context -> new WalkerActor(
                context,
                walkerBroadcastTopic,
                persistenceId,
                dungeonIdentity,
                clientOutbound));
    }

    @Override
    public WalkerState emptyState() {
        log.debug(logShortMessage("empty/asleep state"));
        return new Asleep(context.getSelf().path().name());
    }

    @Override
    public CommandHandler<WalkerCommand, WalkerState> commandHandler() {
        log.debug(logShortMessage("command handler"));

        final var builder = newCommandHandlerBuilder();

        builder.forStateType(Asleep.class)
                .onCommand(WakeUp.class, this::onWakeUp)
                .onAnyCommand((walkerState, command) -> {
                    log.warn(logFullMessage(walkerState, "[{} not allowed while asleep]"), command.getClass().getSimpleName());
                    return Effect().none();
                });

        builder.forStateType(Awake.class)
                .onCommand(Leave.class, this::onLeave)
                .onCommand(UpdateDungeonState.class, this::onFirstUpdateDungeonState)
                .onCommand(UserHeartbeat.class, this::onUserHeartbeat)
                .onAnyCommand((walkerState, command) -> {
                    log.warn(logFullMessage(walkerState, "[{} not allowed while awake]"), command.getClass().getSimpleName());
                    return Effect().none();
                });

        builder.forStateType(Stopped.class)
                .onCommand(Leave.class, this::onLeave)
                .onCommand(UpdateDungeonState.class, this::onUpdateDungeonState)
                .onCommand(UpdateCellState.class, this::onUpdateCellState)
                .onCommand(Move.class, this::onMove)
                .onCommand(UserHeartbeat.class, this::onUserHeartbeat)
                .onAnyCommand((walkerState, command) -> {
                    log.warn(logFullMessage(walkerState, "[{} not allowed while stopped]"), command.getClass().getSimpleName());
                    return Effect().none();
                });

        builder.forStateType(Moving.class)
                .onCommand(Leave.class, this::onLeave)
                .onCommand(UpdateDungeonState.class, this::onUpdateDungeonState)
                .onCommand(UpdateCellState.class, this::onUpdateCellState)
                .onCommand(Move.class, this::onAlreadyMoving)
                .onCommand(Stop.class, this::onStop)
                .onCommand(UserHeartbeat.class, this::onUserHeartbeat)
                .onAnyCommand((walkerState, command) -> {
                    log.warn(logFullMessage(walkerState, "[{} not allowed while moving]"), command.getClass().getSimpleName());
                    return Effect().none();
                });

        builder.forAnyState()
                .onCommand(DungeonHeartbeat.class, this::onDungeonHeartbeat);

        return builder.build();
    }

    @Override
    public SignalHandler<WalkerState> signalHandler() {
        return newSignalHandlerBuilder()
                .onSignal(PostStop.class, this::onPostStop)
                .build();
    }

    private void onPostStop(
            @NonNull final WalkerState state,
            @NonNull final PostStop signal) {
        log.debug(logFullMessage(state, "[on post stop]"));

        this.walkerBroadcastTopic.tell(Topic.unsubscribe(context.getSelf()));
    }

    @SuppressWarnings("java:S1172")
    private Effect<WalkerState> onLeave(
            @NonNull final WalkerState state,
            @NonNull final Leave unused
    ) {
        log.debug(logFullMessage(state, "[on leave]"));

        return Effect()
                .persist(Asleep.of(state))
                .thenRun(persistedWalker -> tellDungeon(
                        persistedWalker.getDungeonId(),
                        new RemoveWalker(persistedWalker.getId())))
                .thenStop();
    }

    protected Effect<WalkerState> onWakeUp(
            @NonNull final WalkerState state,
            @NonNull final WakeUp command
    ) {
        log.debug(logFullMessage(state, "[on wake up (enter dungeon)]: {}"), command);

        // Go to STASIS value
        return Effect()
                .persist(new Awake(state.getId(), dungeonId(state)))
                .thenRun(persistedWalker ->
                        // Tell the dungeonRef that you are alive and want to spawn in the coordinates
                        // The dungeonRef will spawn you somewhere near and tell you that later
                        tellDungeon(
                                persistedWalker.getDungeonId(),
                                new PlaceWalker(
                                        persistedWalker.getId(),
                                        persistedWalker,
                                        command.placingStrategy())));
    }

    private Effect<WalkerState> onFirstUpdateDungeonState(
            @NonNull final WalkerState state,
            @NonNull final UpdateDungeonState command
    ) {
        log.debug(logFullMessage(state, "[on first update dungeon state]: {}"), command);

        // If placed in the dungeon (meaning wasn't another walker placement being broadcasted)
        if (!command.coordinates().containsKey(state.getId())) {
            return Effect().none();
        }

        final var coordinates = command.coordinates().get(state.getId());

        return Effect()
                .persist(Stopped.of(state.updateCoordinates(coordinates)))
                .thenRun(persistedWalker -> clientOutbound.send(
                        EngineMessage
                                .newBuilder()
                                .setTarget(persistedWalker.getId())
                                .setDungeonState(command.toProtoDungeonState())
                                .build()));
    }

    private Effect<WalkerState> onUpdateDungeonState(
            @NonNull final WalkerState state,
            @NonNull final UpdateDungeonState command
    ) {
        log.debug(logFullMessage(state, "[on update dungeon state]: {}"), command);

        return Effect()
                .none()
                .thenRun(persistedWalker -> clientOutbound.send(
                        EngineMessage
                                .newBuilder()
                                .setTarget(persistedWalker.getId())
                                .setDungeonState(command.toProtoDungeonState())
                                .build()));
    }

    private Effect<WalkerState> onUpdateCellState(
            @NonNull final WalkerState state,
            @NonNull final UpdateCellState command
    ) {
        log.debug(logFullMessage(state, "[on update cell state]: {}"), command);

        final var effectBuilder = state.getId().equals(command.id()) && !command.coordinates().equals(state.getCurrentCoordinates())
                ? Effect().persist(Stopped.of(state.updateCoordinates(command.coordinates())))
                : Effect().none();

        return effectBuilder
                .thenRun(persistedWalker -> clientOutbound.send(
                        EngineMessage
                                .newBuilder()
                                .setTarget(persistedWalker.getId())
                                .setDungeonCellState(command.toProtoDungeonCellState())
                                .build()));
    }

    protected Effect<WalkerState> onMove(
            @NonNull final WalkerState state,
            @NonNull final Move command
    ) {
        log.debug(logFullMessage(state, "[on move]: {}"), command);

        return Effect()
                .persist(Moving.of(state))
                // Ask to be moved to the new coordinates
                .thenRun(persistedState -> tellDungeon(
                        dungeonId(persistedState),
                        new MoveWalker(
                                persistedState.getId(),
                                state.getCurrentCoordinates(),
                                List.of(CoordinatesManager
                                        .of(state.getCurrentCoordinates())
                                        .move(command.to(), 1)
                                        .coordinates()))));
    }

    protected Effect<WalkerState> onAlreadyMoving(
            @NonNull final WalkerState state,
            @NonNull final Move command
    ) {
        log.debug(logFullMessage(state, "[on already moving]: {}"), command);

        return Effect()
                .none()
                .thenRun(persistedState -> clientOutbound.send(
                        EngineMessage
                                .newBuilder()
                                .setTarget(persistedState.getId())
                                .setError("Already moving. Wait until stopped")
                                .build()));
    }

    protected Effect<WalkerState> onStop(
            @NonNull final WalkerState state,
            @NonNull final Stop command
    ) {
        log.debug(logFullMessage(state, "[on stop]: {}"), command);

        return Effect()
                .persist(Stopped.of(state))
                .thenRun(persistedState -> clientOutbound.send(
                        EngineMessage
                                .newBuilder()
                                .setTarget(persistedState.getId())
                                .setMessage("Stopped")
                                .build()));
    }

    protected Effect<WalkerState> onDungeonHeartbeat() {
        log.trace(logShortMessage("[on dungeon heartbeat]"));

        return Effect()
                .none()
                .thenRun(persistedState -> clientOutbound.send(
                        EngineMessage
                                .newBuilder()
                                .setTarget(persistedState.getId())
                                .setHeartbeat(EngineHeartbeat.newBuilder().build())
                                .build()));
    }

    @SuppressWarnings("java:S1172")
    protected Effect<WalkerState> onUserHeartbeat(
            @NonNull final WalkerState state,
            @NonNull final UserHeartbeat unused
    ) {
        log.trace(logFullMessage(state, "[on user heartbeat]"));

        return Effect().none();
    }

    private void tellDungeon(
            @NonNull final String entityId,
            @NonNull final DungeonCommand command
    ) {
        ClusterSharding
                .get(context.getSystem())
                .entityRefFor(DungeonActor.ENTITY_TYPE_KEY, entityId)
                .tell(command);
    }

    private String dungeonId(@NonNull final WalkerState state) {
        return isEmpty(state.getDungeonId()) ? dungeonIdentity.id(1) : state.getDungeonId();
    }

    private String stateName(@NonNull final WalkerState state) {
        return state.getClass().getSimpleName();
    }

    private @NonNull String logShortMessage(@NonNull final String message) {
        return "%s[id: %s] %s".formatted(LABEL, context.getSelf().path().name(), message);
    }

    private @NonNull String logFullMessage(
            @NonNull final WalkerState state,
            @NonNull final String message
    ) {
        return "%s[id: %s][state: %S] %s".formatted(LABEL, state.getId(), stateName(state), message);
    }

}

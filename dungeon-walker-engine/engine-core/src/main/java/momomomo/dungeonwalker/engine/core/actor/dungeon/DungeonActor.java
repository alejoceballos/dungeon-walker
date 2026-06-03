package momomomo.dungeonwalker.engine.core.actor.dungeon;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.engine.core.actor.dungeon.command.DungeonCommand;
import momomomo.dungeonwalker.engine.core.actor.dungeon.command.DungeonStateReply;
import momomomo.dungeonwalker.engine.core.actor.dungeon.command.DungeonStateRequest;
import momomomo.dungeonwalker.engine.core.actor.dungeon.command.MoveWalker;
import momomomo.dungeonwalker.engine.core.actor.dungeon.command.SetupDungeon;
import momomomo.dungeonwalker.engine.core.actor.dungeon.command.from.self.DungeonHeartbeatTimeOut;
import momomomo.dungeonwalker.engine.core.actor.dungeon.command.from.walker.PlaceWalker;
import momomomo.dungeonwalker.engine.core.actor.walker.WalkerActor;
import momomomo.dungeonwalker.engine.core.actor.walker.command.WalkerCommand;
import momomomo.dungeonwalker.engine.core.actor.walker.command.from.dungeon.DungeonHeartbeat;
import momomomo.dungeonwalker.engine.core.actor.walker.command.from.dungeon.Stop;
import momomomo.dungeonwalker.engine.core.actor.walker.command.from.dungeon.UpdateDungeonState;
import momomomo.dungeonwalker.engine.domain.model.dungeon.state.DungeonState;
import momomomo.dungeonwalker.engine.domain.model.dungeon.state.InitializedDungeon;
import momomomo.dungeonwalker.engine.domain.model.dungeon.state.UninitializedDungeon;
import org.apache.pekko.actor.typed.ActorRef;
import org.apache.pekko.actor.typed.Behavior;
import org.apache.pekko.actor.typed.PostStop;
import org.apache.pekko.actor.typed.javadsl.ActorContext;
import org.apache.pekko.actor.typed.javadsl.Behaviors;
import org.apache.pekko.actor.typed.javadsl.TimerScheduler;
import org.apache.pekko.actor.typed.pubsub.Topic;
import org.apache.pekko.cluster.sharding.typed.javadsl.ClusterSharding;
import org.apache.pekko.cluster.sharding.typed.javadsl.EntityTypeKey;
import org.apache.pekko.persistence.typed.PersistenceId;
import org.apache.pekko.persistence.typed.state.javadsl.CommandHandler;
import org.apache.pekko.persistence.typed.state.javadsl.DurableStateBehavior;
import org.apache.pekko.persistence.typed.state.javadsl.Effect;
import org.apache.pekko.persistence.typed.state.javadsl.SignalHandler;

import java.time.Duration;

import static java.util.Objects.isNull;

@Slf4j
public class DungeonActor extends DurableStateBehavior<DungeonCommand, DungeonState> {

    private static final String LABEL = "---> [ACTOR - Dungeon]";

    public static final EntityTypeKey<DungeonCommand> ENTITY_TYPE_KEY =
            EntityTypeKey.create(DungeonCommand.class, "dungeonRef-actor-type-key");

    public static final String TIMER_NAME = "%s-periodic-timer";

    private final ActorContext<DungeonCommand> context;
    private final TimerScheduler<DungeonCommand> timers;
    private final ActorRef<Topic.Command<WalkerCommand>> walkerBroadcastTopic;
    private final Duration heartbeatInterval;

    public DungeonActor(
            final ActorContext<DungeonCommand> context,
            final TimerScheduler<DungeonCommand> timers,
            final ActorRef<Topic.Command<WalkerCommand>> walkerBroadcastTopic,
            final Duration heartbeatInterval,
            final PersistenceId persistenceId
    ) {
        this.context = context;
        this.timers = timers;
        this.walkerBroadcastTopic = walkerBroadcastTopic;
        this.heartbeatInterval = heartbeatInterval;

        super(persistenceId);
    }

    public static Behavior<DungeonCommand> create(
            final ActorRef<Topic.Command<WalkerCommand>> walkerBroadcastTopic,
            final Duration heartbeatInterval,
            final PersistenceId persistenceId
    ) {
        log.debug("{}[persistenceId: {}] create", LABEL, persistenceId.toString());
        return Behaviors.setup(context ->
                Behaviors.withTimers(timers ->
                        new DungeonActor(
                                context,
                                timers,
                                walkerBroadcastTopic,
                                heartbeatInterval,
                                persistenceId)));
    }

    @Override
    public DungeonState emptyState() {
        log.debug(logShortMessage("empty/asleep state"));
        return new UninitializedDungeon();
    }

    @Override
    public CommandHandler<DungeonCommand, DungeonState> commandHandler() {
        log.debug(logShortMessage("command handler"));

        final var builder = newCommandHandlerBuilder();

        builder.forStateType(UninitializedDungeon.class)
                .onCommand(SetupDungeon.class, this::onSetupDungeon);

        builder.forStateType(InitializedDungeon.class)
                .onCommand(PlaceWalker.class, this::onPlaceWalker)
                .onCommand(MoveWalker.class, this::onMoveWalker)
                .onCommand(DungeonHeartbeatTimeOut.class, this::onDungeonHeartbeatTimeOut);

        builder.forAnyState()
                .onCommand(DungeonStateRequest.class, this::onDungeonStateRequest);

        return builder.build();
    }

    @Override
    public SignalHandler<DungeonState> signalHandler() {
        return newSignalHandlerBuilder()
                .onSignal(PostStop.class, this::onPostStop)
                .build();
    }

    private void onPostStop(
            final DungeonState state,
            final PostStop postStop) {
        log.debug(logFullMessage(state, "[on post stop]"));
        // TODO: Clean up
    }

    private Effect<DungeonState> onDungeonStateRequest(
            final DungeonState state,
            final DungeonStateRequest command
    ) {
        log.debug(logFullMessage(state, "[on dungeon state request]: {}"), command.replyTo().path().name());
        return Effect()
                .none()
                .thenRun(_ -> command.replyTo().tell(new DungeonStateReply(state)));
    }

    private Effect<DungeonState> onSetupDungeon(
            final DungeonState state,
            final SetupDungeon command
    ) {
        log.debug(logFullMessage(state, "[on setup dungeon]: {}"), command);
        return Effect()
                .persist(InitializedDungeon.of(command.dungeon()))
                .thenRun(_ -> timers
                        .startTimerWithFixedDelay(
                                TIMER_NAME.formatted(entityId()),
                                new DungeonHeartbeatTimeOut(),
                                heartbeatInterval,
                                heartbeatInterval));
    }

    private Effect<DungeonState> onPlaceWalker(
            final DungeonState state,
            final PlaceWalker command
    ) {
        log.debug(logFullMessage(state, "[on place walker]: {}"), command);

        state.placeThing(command.placingStrategy(), command.walker());

        return Effect()
                .persist(state)
                .thenRun(persistedState -> tellAll(
                        new UpdateDungeonState(
                                persistedState.getWidth(),
                                persistedState.getHeight(),
                                persistedState.getWalkersPositions())));
    }

    private Effect<DungeonState> onMoveWalker(
            final DungeonState state,
            final MoveWalker command
    ) {
        log.debug(logFullMessage(state, "[on move walker]: {}"), command);

        final var to = state.moveThing(command.from(), command.toPossibilities());

        return isNull(to) ?
                Effect()
                        .none()
                        .thenRun(_ -> tellWalker(command.walkerEntityId(), new Stop())) :
                Effect()
                        .persist(state)
                        .thenRun(persistedState -> tellAll(
                                new UpdateDungeonState(
                                        persistedState.getWidth(),
                                        persistedState.getHeight(),
                                        persistedState.getWalkersPositions())));
    }

    private Effect<DungeonState> onDungeonHeartbeatTimeOut() {
        log.trace(logShortMessage("[on dungeon heartbeat timeout]"));

        return Effect()
                .none()
                .thenRun(_ -> tellAll(new DungeonHeartbeat()));
    }

    private String entityId() {
        return context.getSelf().path().name();
    }

    private void tellWalker(final String entityId, final WalkerCommand command) {
        ClusterSharding
                .get(context.getSystem())
                .entityRefFor(WalkerActor.ENTITY_TYPE_KEY, entityId)
                .tell(command);
    }

    private void tellAll(final WalkerCommand command) {
        walkerBroadcastTopic.tell(Topic.publish(command));
    }

    private String stateName(@NonNull final DungeonState state) {
        return state.getClass().getSimpleName();
    }

    private @NonNull String logShortMessage(final String message) {
        return "%s[id: %s] %s".formatted(LABEL, context.getSelf().path().name(), message);
    }

    private @NonNull String logFullMessage(final DungeonState state, final String message) {
        return "%s[id: %s][state: %S] %s".formatted(LABEL, entityId(), stateName(state), message);
    }

}

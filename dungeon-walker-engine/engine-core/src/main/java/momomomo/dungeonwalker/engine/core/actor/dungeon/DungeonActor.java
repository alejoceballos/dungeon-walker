package momomomo.dungeonwalker.engine.core.actor.dungeon;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.engine.core.actor.dungeon.command.DungeonCommand;
import momomomo.dungeonwalker.engine.core.actor.dungeon.command.DungeonStateRequest;
import momomomo.dungeonwalker.engine.core.actor.dungeon.command.from.self.DungeonHeartbeatTimeout;
import momomomo.dungeonwalker.engine.core.actor.dungeon.command.from.setup.KeepAliveHeartbeat;
import momomomo.dungeonwalker.engine.core.actor.dungeon.command.from.setup.SetupDungeon;
import momomomo.dungeonwalker.engine.core.actor.dungeon.command.from.walker.MoveWalker;
import momomomo.dungeonwalker.engine.core.actor.dungeon.command.from.walker.PlaceWalker;
import momomomo.dungeonwalker.engine.core.actor.dungeon.command.from.walker.RemoveWalker;
import momomomo.dungeonwalker.engine.core.actor.dungeon.command.to.DungeonStateReply;
import momomomo.dungeonwalker.engine.core.actor.dungeon.command.to.KeepAliveReply;
import momomomo.dungeonwalker.engine.core.actor.walker.WalkerActor;
import momomomo.dungeonwalker.engine.core.actor.walker.command.WalkerCommand;
import momomomo.dungeonwalker.engine.core.actor.walker.command.from.dungeon.DungeonHeartbeat;
import momomomo.dungeonwalker.engine.core.actor.walker.command.from.dungeon.Stop;
import momomomo.dungeonwalker.engine.core.actor.walker.command.from.dungeon.UpdateCellState;
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
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.EMPTY;

@Slf4j
public class DungeonActor extends DurableStateBehavior<DungeonCommand, DungeonState> {

    private static final String LABEL = "---> [ACTOR - Dungeon]";

    public static final EntityTypeKey<DungeonCommand> ENTITY_TYPE_KEY =
            EntityTypeKey.create(DungeonCommand.class, DungeonActor.class.getSimpleName() + "-type-key");

    public static final String TIMER_NAME = "%s-periodic-timer";

    private final ActorContext<DungeonCommand> context;
    private final TimerScheduler<DungeonCommand> timers;
    private final ActorRef<Topic.Command<WalkerCommand>> walkerBroadcastTopic;
    private final Duration heartbeatInterval;

    public DungeonActor(
            @NonNull final ActorContext<DungeonCommand> context,
            @NonNull final TimerScheduler<DungeonCommand> timers,
            @NonNull final ActorRef<Topic.Command<WalkerCommand>> walkerBroadcastTopic,
            @NonNull final Duration heartbeatInterval,
            @NonNull final PersistenceId persistenceId
    ) {
        this.context = context;
        this.timers = timers;
        this.walkerBroadcastTopic = walkerBroadcastTopic;
        this.heartbeatInterval = heartbeatInterval;

        super(persistenceId);
    }

    public static Behavior<DungeonCommand> create(
            @NonNull final ActorRef<Topic.Command<WalkerCommand>> walkerBroadcastTopic,
            @NonNull final Duration heartbeatInterval,
            @NonNull final PersistenceId persistenceId
    ) {
        log.debug("{}[persistenceId: {}] create", LABEL, persistenceId);
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
        log.trace(logShortMessage("command handler"));

        final var builder = newCommandHandlerBuilder();

        builder.forStateType(UninitializedDungeon.class)
                .onCommand(SetupDungeon.class, this::onSetupDungeon);

        builder.forStateType(InitializedDungeon.class)
                .onCommand(PlaceWalker.class, this::onPlaceWalker)
                .onCommand(RemoveWalker.class, this::onRemoveWalker)
                .onCommand(MoveWalker.class, this::onMoveWalker)
                .onCommand(DungeonHeartbeatTimeout.class, this::onDungeonHeartbeatTimeout);

        builder.forAnyState()
                .onCommand(KeepAliveHeartbeat.class, this::onKeepAliveHeartbeat)
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
            @NonNull final DungeonState state,
            @NonNull final PostStop postStop) {
        log.debug(logFullMessage(state, "[on post stop]"));
        // TODO: Clean up
    }

    private Effect<DungeonState> onKeepAliveHeartbeat(
            @NonNull final DungeonState state,
            @NonNull final KeepAliveHeartbeat command
    ) {
        log.trace(logFullMessage(state, "[on keep alive heartbeat]: {}"), command);

        return Effect()
                .none()
                .thenReply(command.replyTo(), _ -> new KeepAliveReply());
    }

    private Effect<DungeonState> onDungeonStateRequest(
            @NonNull final DungeonState state,
            @NonNull final DungeonStateRequest command
    ) {
        log.debug(logFullMessage(state, "[on dungeon state request]: {}"), command.replyTo().path().name());

        return Effect()
                .none()
                .thenReply(command.replyTo(), DungeonStateReply::new);
    }

    private Effect<DungeonState> onSetupDungeon(
            @NonNull final DungeonState state,
            @NonNull final SetupDungeon command
    ) {
        log.debug(logFullMessage(state, "[on setup dungeon]: {}"), command);

        return Effect()
                .persist(InitializedDungeon.of(command.dungeon()));
    }

    private Effect<DungeonState> onPlaceWalker(
            @NonNull final DungeonState state,
            @NonNull final PlaceWalker command
    ) {
        log.debug(logFullMessage(state, "[on place walker]: {}"), command);

        final var coordinates = state.placeWalker(command.placingStrategy(), command.walker());

        return Effect()
                .persist(state)
                .thenRun(persistedState -> tellWalker(
                        command.walkerEntityId(),
                        new UpdateDungeonState(
                                persistedState.getWidth(),
                                persistedState.getHeight(),
                                persistedState.getDungeonState())))
                .thenRun(_ -> tellAll(
                        new UpdateCellState(
                                command.walker().getId(),
                                coordinates)))
                .thenRun(_ -> {
                    final var timerKey = TIMER_NAME.formatted(entityId());

                    if (!timers.isTimerActive(timerKey)) {
                        timers.startTimerWithFixedDelay(
                                TIMER_NAME.formatted(entityId()),
                                new DungeonHeartbeatTimeout(),
                                heartbeatInterval,
                                heartbeatInterval);
                    }
                });
    }

    private Effect<DungeonState> onRemoveWalker(
            @NonNull final DungeonState state,
            @NonNull final RemoveWalker command
    ) {
        log.debug(logFullMessage(state, "[on remove walker]: {}"), command);

        final var coordinates = state.removeWalker(command.walkerEntityId());

        return Effect()
                .persist(state)
                .thenRun(_ -> {
                    if (nonNull(coordinates)) {
                        tellAll(new UpdateCellState(
                                EMPTY,
                                coordinates));
                    }
                });
    }


    private Effect<DungeonState> onMoveWalker(
            @NonNull final DungeonState state,
            @NonNull final MoveWalker command
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
                                new UpdateCellState(
                                        persistedState.at(to).getOccupant().getId(),
                                        to)));
    }

    private Effect<DungeonState> onDungeonHeartbeatTimeout() {
        log.trace(logShortMessage("[on dungeon heartbeat timeout]"));

        return Effect()
                .none()
                .thenRun(_ -> tellAll(new DungeonHeartbeat()));
    }

    private String entityId() {
        return context.getSelf().path().name();
    }

    private void tellWalker(
            @NonNull final String entityId,
            @NonNull final WalkerCommand command
    ) {
        ClusterSharding
                .get(context.getSystem())
                .entityRefFor(WalkerActor.ENTITY_TYPE_KEY, entityId)
                .tell(command);
    }

    private void tellAll(@NonNull final WalkerCommand command) {
        walkerBroadcastTopic.tell(Topic.publish(command));
    }

    private String stateName(@NonNull final DungeonState state) {
        return state.getClass().getSimpleName();
    }

    private @NonNull String logShortMessage(@NonNull final String message) {
        return "%s[id: %s] %s".formatted(LABEL, context.getSelf().path().name(), message);
    }

    private @NonNull String logFullMessage(
            @NonNull final DungeonState state,
            @NonNull final String message
    ) {
        return "%s[id: %s][state: %S] %s".formatted(LABEL, entityId(), stateName(state), message);
    }

}

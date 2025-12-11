package momomomo.dungeonwalker.engine.core.actor.walker;

import akka.actor.typed.javadsl.ActorContext;
import akka.cluster.sharding.typed.javadsl.ClusterSharding;
import akka.cluster.sharding.typed.javadsl.EntityRef;
import akka.persistence.typed.PersistenceId;
import akka.persistence.typed.state.javadsl.CommandHandler;
import akka.persistence.typed.state.javadsl.CommandHandlerBuilderByState;
import akka.persistence.typed.state.javadsl.DurableStateBehavior;
import akka.persistence.typed.state.javadsl.Effect;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.engine.core.actor.dungeon.DungeonActor;
import momomomo.dungeonwalker.engine.core.actor.dungeon.command.DungeonCommand;
import momomomo.dungeonwalker.engine.core.actor.dungeon.command.PlaceWalker;
import momomomo.dungeonwalker.engine.core.actor.walker.command.UpdateCoordinates;
import momomomo.dungeonwalker.engine.core.actor.walker.command.WakeUp;
import momomomo.dungeonwalker.engine.core.actor.walker.command.WalkerCommand;
import momomomo.dungeonwalker.engine.domain.model.walker.state.Awake;
import momomomo.dungeonwalker.engine.domain.model.walker.state.Moving;
import momomomo.dungeonwalker.engine.domain.model.walker.state.Sleeping;
import momomomo.dungeonwalker.engine.domain.model.walker.state.Stopped;
import momomomo.dungeonwalker.engine.domain.model.walker.state.WalkerState;

@Slf4j
public abstract class WalkerActor extends DurableStateBehavior<WalkerCommand, WalkerState> {

    private static final String LABEL = "---> [ACTOR - Walker]";

    protected final ActorContext<WalkerCommand> context;

    private final ClusterSharding cluster;

    protected WalkerActor(
            @NonNull final ActorContext<WalkerCommand> context,
            @NonNull final PersistenceId persistenceId) {
        log.debug("{}[Path: {}][State: null] constructor", LABEL, context.getSelf().toString());
        this.cluster = ClusterSharding.get(context.getSystem());
        this.context = context;
        super(persistenceId);
    }

    @Override
    public CommandHandler<WalkerCommand, WalkerState> commandHandler() {
        log.debug("{}[Path: {}][State: ?] command handler", LABEL, actorPath());

        final var builder = newCommandHandlerBuilder();

        builder.forStateType(Sleeping.class)
                .onCommand(WakeUp.class, this::onWakeUp);

        builder.forStateType(Awake.class)
                .onCommand(UpdateCoordinates.class, this::onUpdateCoordinates);

        setStandingStillStateCommands(builder.forStateType(Stopped.class));
        setOnTheMoveStateCommands(builder.forStateType(Moving.class));

        return builder.build();
    }

    protected abstract void setStandingStillStateCommands(
            @NonNull final CommandHandlerBuilderByState<WalkerCommand, Stopped, WalkerState> builder);

    protected abstract void setOnTheMoveStateCommands(
            @NonNull final CommandHandlerBuilderByState<WalkerCommand, Moving, WalkerState> builder);

    protected Effect<WalkerState> onWakeUp(
            @NonNull final WalkerState state,
            @NonNull final WakeUp command) {
        log.debug("{}[Path: {}][State: {}] on enter dungeon", LABEL, actorPath(), state(state));

        final var walker = new Awake(
                entityId(),
                state.getType(),
                command.movingStrategy(),
                command.dungeonEntityId());

        // Go to STASIS value
        return Effect()
                .persist(walker)
                .thenRun(_ ->
                        // Tell the dungeonRef that you are alive and want to spawn in the coordinates
                        // The dungeonRef will spawn you somewhere near and tell you that later
                        dungeonEntityRef(walker.getDungeonId())
                                .tell(new PlaceWalker(
                                        entityId(),
                                        state.getType(),
                                        walker,
                                        command.placingStrategy())));
    }

    protected abstract Effect<WalkerState> onUpdateCoordinates(
            @NonNull final WalkerState state,
            @NonNull final UpdateCoordinates command);

    protected String actorPath() {
        return context.getSelf().path().name();
    }

    protected String entityId() {
        return context.getSelf().path().name();
    }

    protected String state(@NonNull WalkerState state) {
        return state.getClass().getSimpleName();
    }

    protected EntityRef<DungeonCommand> dungeonEntityRef(@NonNull final String entityId) {
        return cluster.entityRefFor(DungeonActor.ENTITY_TYPE_KEY, entityId);
    }

}

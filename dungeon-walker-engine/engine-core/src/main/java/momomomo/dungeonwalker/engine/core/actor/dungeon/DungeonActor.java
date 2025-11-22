package momomomo.dungeonwalker.engine.core.actor.dungeon;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.cluster.sharding.typed.javadsl.ClusterSharding;
import akka.cluster.sharding.typed.javadsl.EntityRef;
import akka.cluster.sharding.typed.javadsl.EntityTypeKey;
import akka.persistence.typed.PersistenceId;
import akka.persistence.typed.state.javadsl.CommandHandler;
import akka.persistence.typed.state.javadsl.DurableStateBehavior;
import akka.persistence.typed.state.javadsl.Effect;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.engine.core.actor.dungeon.command.DungeonCommand;
import momomomo.dungeonwalker.engine.core.actor.dungeon.command.MoveWalker;
import momomomo.dungeonwalker.engine.core.actor.dungeon.command.PlaceWalker;
import momomomo.dungeonwalker.engine.core.actor.dungeon.command.SetupDungeon;
import momomomo.dungeonwalker.engine.core.actor.dungeon.state.InitializedDungeon;
import momomomo.dungeonwalker.engine.core.actor.dungeon.state.UninitializedDungeon;
import momomomo.dungeonwalker.engine.core.actor.walker.WalkerActor;
import momomomo.dungeonwalker.engine.core.actor.walker.command.StandStill;
import momomomo.dungeonwalker.engine.core.actor.walker.command.UpdateCoordinates;
import momomomo.dungeonwalker.engine.core.actor.walker.command.WalkerCommand;
import momomomo.dungeonwalker.engine.domain.model.dungeon.Dungeon;

import static java.util.Objects.isNull;

@Slf4j
public class DungeonActor extends DurableStateBehavior<DungeonCommand, Dungeon> {

    public static final EntityTypeKey<DungeonCommand> ENTITY_TYPE_KEY =
            EntityTypeKey.create(DungeonCommand.class, "dungeonRef-actor-type-key");

    private final ActorContext<DungeonCommand> context;

    private final ClusterSharding cluster;

    public DungeonActor(
            final ActorContext<DungeonCommand> context,
            final PersistenceId persistenceId) {
        log.debug("---> [ACTOR - Dungeon][path: {}] constructor", context.getSelf().path().toString());
        this.cluster = ClusterSharding.get(context.getSystem());
        this.context = context;
        super(persistenceId);
    }

    public static Behavior<DungeonCommand> create(final PersistenceId persistenceId) {
        log.debug("---> [ACTOR - Dungeon][persistenceId: {}] create", persistenceId.toString());
        return Behaviors.setup(context -> new DungeonActor(context, persistenceId));
    }

    @Override
    public Dungeon emptyState() {
        log.debug("---> [ACTOR - Dungeon][path: {}] empty state", context.getSelf().path().toString());
        return new UninitializedDungeon();
    }

    @Override
    public CommandHandler<DungeonCommand, Dungeon> commandHandler() {
        log.debug("---> [ACTOR - Dungeon][path: {}] command handler", context.getSelf().path().toString());

        final var builder = newCommandHandlerBuilder();

        builder.forNullState()
                .onCommand(SetupDungeon.class, this::onSetupDungeon);
        builder.forStateType(UninitializedDungeon.class)
                .onCommand(SetupDungeon.class, this::onSetupDungeon);
        builder.forStateType(InitializedDungeon.class)
                .onCommand(PlaceWalker.class, this::onPlaceWalker)
                .onCommand(MoveWalker.class, this::onMoveWalker);

        return builder.build();
    }

    private Effect<Dungeon> onSetupDungeon(
            final Dungeon state,
            final SetupDungeon command) {
        log.debug("---> [ACTOR - Dungeon][path: {}] on setup dungeonRef", context.getSelf().path().toString());

        command.dungeon().print();

        return Effect().persist(InitializedDungeon.of(command.dungeon()));
    }

    private Effect<Dungeon> onPlaceWalker(
            final Dungeon state,
            final PlaceWalker command) {
        log.debug("---> [ACTOR - Dungeon][path: {}] on place walker", context.getSelf().path().toString());

        final var coordinates = command.placingStrategy().placingCoordinates(state);
        state.at(coordinates).occupy(command.walker());

        state.print();

        return Effect()
                .persist(state)
                .thenRun(_ -> walkerEntityRef(command.walkerEntityId()).tell(
                        new UpdateCoordinates(
                                context.getSelf().path().name(),
                                coordinates)));
    }

    private Effect<Dungeon> onMoveWalker(
            final Dungeon state,
            final MoveWalker command) {
        log.debug("---> [ACTOR - Dungeon][path: {}] on move walker", context.getSelf().path().toString());

        final var to = command.toPossibilities()
                .stream()
                .filter(coordinates -> state.at(coordinates).isFree())
                .findFirst()
                .orElse(null);

        if (isNull(to)) {
            return Effect()
                    .none()
                    .thenRun(_ -> walkerEntityRef(command.walkerEntityId())
                            .tell(new StandStill(context.getSelf().path().name())));
        }

        // Get walker from its current position
        final var walker = state.at(command.from()).getOccupant();
        // Put it into the new position
        state.at(to).occupy(walker);
        // Remove it from the old position
        state.at(command.from()).vacate();

        state.print();

        return Effect()
                .persist(state)
                .thenRun(_ ->
                        walkerEntityRef(command.walkerEntityId())
                                .tell(new UpdateCoordinates(context.getSelf().path().name(), to)));
    }

    private EntityRef<WalkerCommand> walkerEntityRef(final String entityId) {
        return cluster.entityRefFor(WalkerActor.ENTITY_TYPE_KEY, entityId);
    }

}

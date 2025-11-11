package momomomo.dungeonwalker.engine.core.actor.dungeon;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
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
import momomomo.dungeonwalker.engine.core.actor.walker.command.StandStill;
import momomomo.dungeonwalker.engine.core.actor.walker.command.UpdateCoordinates;
import momomomo.dungeonwalker.engine.domain.model.dungeon.Dungeon;

import static java.util.Objects.isNull;

@Slf4j
public class DungeonActor extends DurableStateBehavior<DungeonCommand, Dungeon> {

    public static final EntityTypeKey<DungeonCommand> ENTITY_TYPE_KEY =
            EntityTypeKey.create(DungeonCommand.class, "dungeonRef-actor-type-key");

    private final ActorContext<DungeonCommand> context;

    public DungeonActor(
            final ActorContext<DungeonCommand> context,
            final PersistenceId persistenceId) {
        log.debug("[ACTOR - Dungeon][entityId: {}][id: {}] constructor. Context path name: {}",
                persistenceId.entityId(), persistenceId.id(), context.getSelf().path().name());
        this.context = context;
        super(persistenceId);
    }

    public static Behavior<DungeonCommand> create(final PersistenceId persistenceId) {
        log.debug("[ACTOR - Dungeon][entityId: {}][id: {}] create", persistenceId.entityId(), persistenceId.id());
        return Behaviors.setup(context -> new DungeonActor(context, persistenceId));
    }

    @Override
    public Dungeon emptyState() {
        log.debug("[ACTOR - Dungeon][entityId: {}][id: {}] empty state. Context path name: {}",
                persistenceId().entityId(), persistenceId().id(), context.getSelf().path().name());
        return new UninitializedDungeon();
    }

    @Override
    public CommandHandler<DungeonCommand, Dungeon> commandHandler() {
        log.debug("[ACTOR - Dungeon][entityId: {}][id: {}] command handler. Context path name: {}",
                persistenceId().entityId(), persistenceId().id(), context.getSelf().path().name());

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
        log.debug("[ACTOR - Dungeon][entityId: {}][id: {}] on setup dungeonRef. Context path name: {}",
                persistenceId().entityId(), persistenceId().id(), context.getSelf().path().name());

        return Effect().persist(InitializedDungeon.of(command.dungeon()));
    }

    private Effect<Dungeon> onPlaceWalker(
            final Dungeon state,
            final PlaceWalker command) {
        log.debug("[ACTOR - Dungeon][entityId: {}][id: {}] on place walker \"{}\". Context path name: {}",
                persistenceId().entityId(),
                persistenceId().id(),
                command.walkerRef().path().name(),
                context.getSelf().path().name());

        final var coordinates = command.placingStrategy().placingCoordinates(state);
        state.at(coordinates).occupy(command.walker());

        return Effect()
                .persist(state)
                .thenRun(_ -> command.walkerRef().tell(
                        new UpdateCoordinates(
                                context.getSelf(),
                                coordinates)));
    }

    private Effect<Dungeon> onMoveWalker(
            final Dungeon state,
            final MoveWalker command) {
        log.debug("[ACTOR - Dungeon][entityId: {}][id: {}] on move walker \"{}\". Context path name: {}",
                persistenceId().entityId(),
                persistenceId().id(),
                command.walkerRef().path().name(),
                context.getSelf().path().name());

        final var to = command.toPossibilities()
                .stream()
                .filter(coordinates -> state.at(coordinates).isFree())
                .findFirst()
                .orElse(null);

        if (isNull(to)) {
            return Effect()
                    .none()
                    .thenRun(_ -> command.walkerRef().tell(new StandStill(context.getSelf())));
        }

        // Get walker from its current position
        final var walker = state.at(command.from()).getOccupant();
        // Put it into the new position
        state.at(to).occupy(walker);
        // Remove it from the old position
        state.at(command.from()).vacate();

        return Effect()
                .persist(state)
                .thenRun(_ ->
                        command.walkerRef().tell(new UpdateCoordinates(context.getSelf(), to)));
    }

}

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
import momomomo.dungeonwalker.engine.core.actor.walker.AutomatedWalkerActor;
import momomomo.dungeonwalker.engine.core.actor.walker.UserWalkerActor;
import momomomo.dungeonwalker.engine.core.actor.walker.command.StandStill;
import momomomo.dungeonwalker.engine.core.actor.walker.command.UpdateCoordinates;
import momomomo.dungeonwalker.engine.core.actor.walker.command.WalkerCommand;
import momomomo.dungeonwalker.engine.domain.model.dungeon.Dungeon;
import momomomo.dungeonwalker.engine.domain.model.walker.WalkerType;

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
        super(persistenceId);
        this.cluster = ClusterSharding.get(context.getSystem());
        this.context = context;
        log.debug("---> [ACTOR - Dungeon][path: {}] constructor", actorPath());
    }

    public static Behavior<DungeonCommand> create(final PersistenceId persistenceId) {
        log.debug("---> [ACTOR - Dungeon][persistenceId: {}] create", persistenceId.toString());
        return Behaviors.setup(context -> new DungeonActor(context, persistenceId));
    }

    @Override
    public Dungeon emptyState() {
        log.debug("---> [ACTOR - Dungeon][path: {}] empty state", actorPath());
        return new UninitializedDungeon();
    }

    @Override
    public CommandHandler<DungeonCommand, Dungeon> commandHandler() {
        log.debug("---> [ACTOR - Dungeon][path: {}] command handler", actorPath());

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
        log.debug("---> [ACTOR - Dungeon][path: {}] on setup dungeonRef", actorPath());

        command.dungeon().print();

        return Effect().persist(InitializedDungeon.of(command.dungeon()));
    }

    private Effect<Dungeon> onPlaceWalker(
            final Dungeon state,
            final PlaceWalker command) {
        log.debug("---> [ACTOR - Dungeon][path: {}] on place walker", actorPath());

        final var coordinates = state.placeThing(
                command.placingStrategy(),
                command.walker());

        state.print();

        return Effect()
                .persist(state)
                .thenRun(_ -> walkerEntityRef(command.walkerType(), command.walkerEntityId())
                        .tell(new UpdateCoordinates(
                                entityId(),
                                coordinates)));
    }

    private Effect<Dungeon> onMoveWalker(
            final Dungeon state,
            final MoveWalker command) {
        log.debug("---> [ACTOR - Dungeon][path: {}] on move walker", actorPath());

        final var to = state.moveThing(
                command.from(),
                command.toPossibilities());

        state.print();

        return isNull(to) ?
                Effect()
                        .none()
                        .thenRun(_ ->
                                walkerEntityRef(command.walkerType(), command.walkerEntityId())
                                        .tell(new StandStill(entityId()))) :
                Effect()
                        .persist(state)
                        .thenRun(_ ->
                                walkerEntityRef(command.walkerType(), command.walkerEntityId())
                                        .tell(new UpdateCoordinates(entityId(), to)));
    }

    private String actorPath() {
        return context.getSelf().path().toString();
    }

    private String entityId() {
        return context.getSelf().path().name();
    }

    private EntityRef<WalkerCommand> walkerEntityRef(final WalkerType walkerType, final String entityId) {
        return switch (walkerType) {
            case USER -> cluster.entityRefFor(UserWalkerActor.ENTITY_TYPE_KEY, entityId);
            case AUTOMATED -> cluster.entityRefFor(AutomatedWalkerActor.ENTITY_TYPE_KEY, entityId);
        };
    }

}

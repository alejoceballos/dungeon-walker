package momomomo.dungeonwalker.engine.core.actor.walker;

import akka.actor.typed.javadsl.ActorContext;
import akka.cluster.sharding.typed.javadsl.ClusterSharding;
import akka.cluster.sharding.typed.javadsl.EntityRef;
import akka.cluster.sharding.typed.javadsl.EntityTypeKey;
import akka.persistence.typed.PersistenceId;
import akka.persistence.typed.state.javadsl.CommandHandler;
import akka.persistence.typed.state.javadsl.DurableStateBehavior;
import akka.persistence.typed.state.javadsl.Effect;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.engine.core.actor.dungeon.DungeonActor;
import momomomo.dungeonwalker.engine.core.actor.dungeon.command.DungeonCommand;
import momomomo.dungeonwalker.engine.core.actor.dungeon.command.MoveWalker;
import momomomo.dungeonwalker.engine.core.actor.dungeon.command.PlaceWalker;
import momomomo.dungeonwalker.engine.core.actor.walker.command.AskToEnterTheDungeon;
import momomomo.dungeonwalker.engine.core.actor.walker.command.GetMoving;
import momomomo.dungeonwalker.engine.core.actor.walker.command.StandStill;
import momomomo.dungeonwalker.engine.core.actor.walker.command.UpdateCoordinates;
import momomomo.dungeonwalker.engine.core.actor.walker.command.WalkerCommand;
import momomomo.dungeonwalker.engine.core.actor.walker.state.Awaken;
import momomomo.dungeonwalker.engine.core.actor.walker.state.OnTheMove;
import momomomo.dungeonwalker.engine.core.actor.walker.state.StandingStill;
import momomomo.dungeonwalker.engine.core.actor.walker.state.WaitingToEnter;
import momomomo.dungeonwalker.engine.domain.model.walker.Walker;
import momomomo.dungeonwalker.engine.domain.model.walker.moving.SameDirectionOrRandomOtherwise;

@Slf4j
public abstract class WalkerActor extends DurableStateBehavior<WalkerCommand, Walker> {

    public static final EntityTypeKey<WalkerCommand> ENTITY_TYPE_KEY =
            EntityTypeKey.create(WalkerCommand.class, "walkerRef-actor-type-key");

    protected final ActorContext<WalkerCommand> context;

    private final ClusterSharding cluster;

    protected WalkerActor(
            final ActorContext<WalkerCommand> context,
            final PersistenceId persistenceId) {
        log.debug("---> [ACTOR - Walker][path: {}] constructor", context.getSelf().toString());
        this.cluster = ClusterSharding.get(context.getSystem());
        this.context = context;
        super(persistenceId);
    }

    @Override
    public Walker emptyState() {
        log.debug("---> [ACTOR - Walker][path: {}] empty state", actorPath());
        return new Awaken(entityId(), new SameDirectionOrRandomOtherwise());
    }

    @Override
    public CommandHandler<WalkerCommand, Walker> commandHandler() {
        log.debug("---> [ACTOR - Walker][path: {}] command handler", actorPath());

        final var builder = newCommandHandlerBuilder();

        builder.forStateType(Awaken.class)
                .onCommand(AskToEnterTheDungeon.class, this::onAskingToEnterTheDungeon);

        builder.forStateType(WaitingToEnter.class)
                .onCommand(UpdateCoordinates.class, this::onUpdateCoordinates);

        builder.forStateType(StandingStill.class)
                .onCommand(UpdateCoordinates.class, this::onUpdateCoordinates)
                .onCommand(GetMoving.class, this::onMove);

        builder.forStateType(OnTheMove.class)
                .onCommand(UpdateCoordinates.class, this::onUpdateCoordinates)
                .onCommand(GetMoving.class, this::onAlreadyMoving)
                .onCommand(StandStill.class, this::onStandStill);

        return builder.build();
    }

    private Effect<Walker> onAskingToEnterTheDungeon(
            final Walker state,
            final AskToEnterTheDungeon command) {
        log.debug("---> [ACTOR - Walker][path: {}] on enter dungeon", actorPath());

        final var walker = new WaitingToEnter(entityId(), command.movingStrategy());

        // Go to STASIS state
        return Effect()
                .persist(walker)
                .thenRun(_ ->
                        // Tell the dungeonRef that you are alive and want to spawn in the coordinates
                        // The dungeonRef will spawn you somewhere near and tell you that later
                        dungeonEntityRef(command.dungeonEntityId())
                                .tell(new PlaceWalker(
                                        entityId(),
                                        walker,
                                        command.placingStrategy())));
    }

    protected abstract Effect<Walker> onUpdateCoordinates(
            final Walker state,
            final UpdateCoordinates command);

    private Effect<Walker> onMove(
            final Walker state,
            final GetMoving command) {
        log.debug("---> [ACTOR - Walker][path: {}] on move", actorPath());

        // Ask to be moved to the new coordinates
        dungeonEntityRef(command.dungeonEntityId())
                .tell(new MoveWalker(
                        entityId(),
                        state.getCurrentCoordinates(),
                        // Calculate new coordinates
                        state.possibleDirections()));

        return Effect().persist(OnTheMove.of(state));
    }

    private Effect<Walker> onAlreadyMoving(
            final Walker state,
            final GetMoving command) {
        log.debug("---> [ACTOR - Walker][path: {}] on already moving", actorPath());

        return Effect().none();
    }

    protected abstract Effect<Walker> onStandStill(
            final Walker state,
            final StandStill command);

    protected String actorPath() {
        return context.getSelf().path().toString();
    }

    private String entityId() {
        return context.getSelf().path().name();
    }

    private EntityRef<DungeonCommand> dungeonEntityRef(final String entityId) {
        return cluster.entityRefFor(DungeonActor.ENTITY_TYPE_KEY, entityId);
    }

}

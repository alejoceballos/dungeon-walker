package momomomo.dungeonwalker.engine.core.actor.walker;

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

import java.util.Objects;

import static java.time.Duration.ofMillis;

@Slf4j
public class WalkerActor extends DurableStateBehavior<WalkerCommand, Walker> {

    public static final EntityTypeKey<WalkerCommand> ENTITY_TYPE_KEY =
            EntityTypeKey.create(WalkerCommand.class, "walkerRef-actor-type-key");

    private final ActorContext<WalkerCommand> context;
    private final ClusterSharding cluster;

    private WalkerActor(
            final ActorContext<WalkerCommand> context,
            final PersistenceId persistenceId) {
        log.debug("---> [ACTOR - Walker][path: {}] constructor", context.getSelf().toString());
        this.cluster = ClusterSharding.get(context.getSystem());
        this.context = context;
        super(persistenceId);
    }

    public static Behavior<WalkerCommand> create(final PersistenceId persistenceId) {
        log.debug("---> [ACTOR - Dungeon][persistenceId: {}] create", persistenceId.toString());
        return Behaviors.setup(context -> new WalkerActor(context, persistenceId));
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

    private Effect<Walker> onUpdateCoordinates(
            final Walker state,
            final UpdateCoordinates command) {
        log.debug("---> [ACTOR - Walker][path: {}] on update coordinates", actorPath());

        final var effect = Objects.equals(state.getCurrentCoordinates(), command.coordinates()) ?
                Effect().none() :
                Effect().persist(StandingStill.of(state.updateCoordinates(command.coordinates())));

        return effect.thenRun(_ -> {
            // 1. Send update to a client listener (tell to "Actor Broadcaster")
            // 2. Restart the timer to start moving again
            startTimerToGetMoving(command.dungeonEntityId());
        });
    }

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

    private Effect<Walker> onStandStill(
            final Walker state,
            final StandStill command) {
        log.debug("---> [ACTOR - Walker][path: {}] on stand still", actorPath());

        return Effect().none().thenRun(_ -> startTimerToGetMoving(command.dungeonEntityId()));
    }

    private void startTimerToGetMoving(final String dungeonEntityId) {
        // Start a counter-delay (in the future, based on the walker velocity). When the timer reaches zero, it will
        // send to the dungeonRef the next movements it wants to take

        // Wait for some while (it could be the walker speed, the lower the value, the faster it moves) and then
        // send a self-command to start calculating where to go and to really move
        context.scheduleOnce(
                ofMillis(1000),
                context.getSelf(),
                new GetMoving(dungeonEntityId));
    }

    private String actorPath() {
        return context.getSelf().path().toString();
    }

    private String entityId() {
        return context.getSelf().path().name();
    }

    private EntityRef<DungeonCommand> dungeonEntityRef(final String entityId) {
        return cluster.entityRefFor(DungeonActor.ENTITY_TYPE_KEY, entityId);
    }

}

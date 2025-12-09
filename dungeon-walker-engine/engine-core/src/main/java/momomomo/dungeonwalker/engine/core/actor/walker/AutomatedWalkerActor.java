package momomomo.dungeonwalker.engine.core.actor.walker;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.cluster.sharding.typed.javadsl.EntityTypeKey;
import akka.persistence.typed.PersistenceId;
import akka.persistence.typed.state.javadsl.CommandHandlerBuilderByState;
import akka.persistence.typed.state.javadsl.Effect;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.engine.core.actor.dungeon.command.MoveWalker;
import momomomo.dungeonwalker.engine.core.actor.walker.command.GetMoving;
import momomomo.dungeonwalker.engine.core.actor.walker.command.StandStill;
import momomomo.dungeonwalker.engine.core.actor.walker.command.UpdateCoordinates;
import momomomo.dungeonwalker.engine.core.actor.walker.command.WalkerCommand;
import momomomo.dungeonwalker.engine.core.actor.walker.state.Awaken;
import momomomo.dungeonwalker.engine.core.actor.walker.state.OnTheMove;
import momomomo.dungeonwalker.engine.core.actor.walker.state.StandingStill;
import momomomo.dungeonwalker.engine.domain.model.walker.Walker;
import momomomo.dungeonwalker.engine.domain.model.walker.moving.SameDirectionOrRandomOtherwise;

import java.util.Objects;

import static java.time.Duration.ofMillis;

@Slf4j
public class AutomatedWalkerActor extends WalkerActor {

    public static final EntityTypeKey<WalkerCommand> ENTITY_TYPE_KEY =
            EntityTypeKey.create(WalkerCommand.class, "walkerRef-auto-actor-type-key");

    private AutomatedWalkerActor(
            @NonNull final ActorContext<WalkerCommand> context,
            @NonNull final PersistenceId persistenceId) {
        super(context, persistenceId);
    }

    public static Behavior<WalkerCommand> create(@NonNull final PersistenceId persistenceId) {
        log.debug("---> [ACTOR - Auto Walker][persistenceId: {}] create", persistenceId.toString());
        return Behaviors.setup(context -> new AutomatedWalkerActor(context, persistenceId));
    }

    @Override
    public Walker emptyState() {
        log.debug("---> [ACTOR - Auto Walker][path: {}] empty state", actorPath());
        return new Awaken(entityId(), new SameDirectionOrRandomOtherwise());
    }

    @Override
    protected void setStandingStillStateCommands(
            @NonNull final CommandHandlerBuilderByState<WalkerCommand, StandingStill, Walker> builder
    ) {
        builder
                .onCommand(UpdateCoordinates.class, this::onUpdateCoordinates)
                .onCommand(GetMoving.class, this::onMove);
    }

    @Override
    protected void setonTheMoveStateCommands(
            @NonNull CommandHandlerBuilderByState<WalkerCommand, OnTheMove, Walker> builder
    ) {
        builder
                .onCommand(UpdateCoordinates.class, this::onUpdateCoordinates)
                .onCommand(GetMoving.class, this::onAlreadyMoving)
                .onCommand(StandStill.class, this::onStandStill);
    }

    @Override
    protected Effect<Walker> onUpdateCoordinates(
            @NonNull final Walker state,
            @NonNull final UpdateCoordinates command) {
        log.debug("---> [ACTOR - Auto Walker][path: {}] on update coordinates", actorPath());

        final var effect = Objects.equals(state.getCurrentCoordinates(), command.coordinates()) ?
                Effect().none() :
                Effect().persist(StandingStill.of(state.updateCoordinates(command.coordinates())));

        return effect.thenRun(_ -> {
            // 1. Send update to a client listener (tell to "Actor Broadcaster")
            // 2. Restart the timer to start moving again
            startTimerToGetMoving(state, command.dungeonEntityId());
        });
    }

    protected Effect<Walker> onMove(
            @NonNull final Walker state,
            @NonNull final GetMoving command) {
        log.debug("---> [ACTOR - Auto Walker][path: {}] on move", actorPath());

        // Ask to be moved to the new coordinates
        dungeonEntityRef(command.dungeonEntityId())
                .tell(new MoveWalker(
                        entityId(),
                        command.walkerType(),
                        state.getCurrentCoordinates(),
                        command.possibleCoordinatesTo()));

        return Effect().persist(OnTheMove.of(state));
    }

    protected Effect<Walker> onAlreadyMoving(
            @NonNull final Walker state,
            @NonNull final GetMoving command) {
        log.debug("---> [ACTOR - Auto Walker][path: {}] on already moving", actorPath());

        return Effect().none();
    }

    protected Effect<Walker> onStandStill(
            @NonNull final Walker state,
            @NonNull final StandStill command) {
        log.debug("---> [ACTOR - Auto Walker][path: {}] on stand still", actorPath());

        return Effect().none().thenRun(_ -> startTimerToGetMoving(state, command.dungeonEntityId()));
    }

    private void startTimerToGetMoving(
            @NonNull final Walker state,
            @NonNull final String dungeonEntityId) {
        // Start a counter-delay (in the future, based on the walker velocity). When the timer reaches zero, it will
        // send to the dungeonRef the next movements it wants to take

        // Wait for some while (it could be the walker speed, the lower the value, the faster it moves) and then
        // send a self-command to start calculating where to go and to really move
        context.scheduleOnce(
                ofMillis(1000),
                context.getSelf(),
                new GetMoving(
                        dungeonEntityId,
                        WalkerType.AUTOMATED,
                        // Calculate new coordinates
                        state.possibleCoordinatesTo()));
    }

}

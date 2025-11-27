package momomomo.dungeonwalker.engine.core.actor.walker;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.persistence.typed.PersistenceId;
import akka.persistence.typed.state.javadsl.Effect;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.engine.core.actor.walker.command.GetMoving;
import momomomo.dungeonwalker.engine.core.actor.walker.command.StandStill;
import momomomo.dungeonwalker.engine.core.actor.walker.command.UpdateCoordinates;
import momomomo.dungeonwalker.engine.core.actor.walker.command.WalkerCommand;
import momomomo.dungeonwalker.engine.core.actor.walker.state.StandingStill;
import momomomo.dungeonwalker.engine.domain.model.walker.Walker;

import java.util.Objects;

import static java.time.Duration.ofMillis;

@Slf4j
public class AutomatedWalkerActor extends WalkerActor {

    private AutomatedWalkerActor(
            final ActorContext<WalkerCommand> context,
            final PersistenceId persistenceId) {
        super(context, persistenceId);
    }

    public static Behavior<WalkerCommand> create(final PersistenceId persistenceId) {
        log.debug("---> [ACTOR - Dungeon][persistenceId: {}] create", persistenceId.toString());
        return Behaviors.setup(context -> new AutomatedWalkerActor(context, persistenceId));
    }

    @Override
    protected Effect<Walker> onUpdateCoordinates(
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

    @Override
    protected Effect<Walker> onStandStill(
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

}

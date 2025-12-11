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
import momomomo.dungeonwalker.engine.core.actor.walker.command.Stop;
import momomomo.dungeonwalker.engine.core.actor.walker.command.UpdateCoordinates;
import momomomo.dungeonwalker.engine.core.actor.walker.command.WalkerCommand;
import momomomo.dungeonwalker.engine.domain.model.walker.moving.SameDirectionOrRandomOtherwise;
import momomomo.dungeonwalker.engine.domain.model.walker.state.Moving;
import momomomo.dungeonwalker.engine.domain.model.walker.state.Sleeping;
import momomomo.dungeonwalker.engine.domain.model.walker.state.Stopped;
import momomomo.dungeonwalker.engine.domain.model.walker.state.WalkerState;

import java.util.Objects;

import static java.time.Duration.ofMillis;
import static momomomo.dungeonwalker.engine.domain.model.walker.WalkerType.AUTOMATED;

@Slf4j
public class AutomatedWalkerActor extends WalkerActor {

    private static final String LABEL = "---> [ACTOR - Auto Walker]";

    public static final EntityTypeKey<WalkerCommand> ENTITY_TYPE_KEY =
            EntityTypeKey.create(WalkerCommand.class, "walkerRef-auto-actor-type-key");

    private AutomatedWalkerActor(
            @NonNull final ActorContext<WalkerCommand> context,
            @NonNull final PersistenceId persistenceId) {
        log.debug("{}[Path: {}][State: null] constructor", LABEL, context.getSelf().path().name());
        super(context, persistenceId);
    }

    public static Behavior<WalkerCommand> create(@NonNull final PersistenceId persistenceId) {
        log.debug("{}[persistenceId: {}] create", LABEL, persistenceId);
        return Behaviors.setup(context -> new AutomatedWalkerActor(context, persistenceId));
    }

    @Override
    public WalkerState emptyState() {
        log.debug("{}[Path: {}][State: {}] empty value", LABEL, actorPath(), Sleeping.class.getSimpleName());
        return new Sleeping(entityId(), AUTOMATED, new SameDirectionOrRandomOtherwise());
    }

    @Override
    protected void setStandingStillStateCommands(
            @NonNull final CommandHandlerBuilderByState<WalkerCommand, Stopped, WalkerState> builder
    ) {
        builder
                .onCommand(UpdateCoordinates.class, this::onUpdateCoordinates)
                .onCommand(GetMoving.class, this::onMove);
    }

    @Override
    protected void setOnTheMoveStateCommands(
            @NonNull CommandHandlerBuilderByState<WalkerCommand, Moving, WalkerState> builder
    ) {
        builder
                .onCommand(UpdateCoordinates.class, this::onUpdateCoordinates)
                .onCommand(GetMoving.class, this::onAlreadyMoving)
                .onCommand(Stop.class, this::onStop);
    }

    @Override
    protected Effect<WalkerState> onUpdateCoordinates(
            @NonNull final WalkerState state,
            @NonNull final UpdateCoordinates command) {
        log.debug("{}[Path: {}][State: {}] on update coordinates", LABEL, actorPath(), state(state));

        final var effect = Objects.equals(state.getCurrentCoordinates(), command.coordinates()) ?
                Effect().none() :
                Effect().persist(Stopped.of(state.updateCoordinates(command.coordinates())));

        return effect.thenRun(_ -> {
            // 1. Send update to a client listener (tell to "Actor Broadcaster")
            // 2. Restart the timer to start moving again
            startTimerToGetMoving(state);
        });
    }

    protected Effect<WalkerState> onMove(
            @NonNull final WalkerState state,
            @NonNull final GetMoving command) {
        log.debug("{}[Path: {}][State: {}] on move", LABEL, actorPath(), state(state));

        // Ask to be moved to the new coordinates
        dungeonEntityRef(state.getDungeonId())
                .tell(new MoveWalker(
                        entityId(),
                        state.getType(),
                        state.getCurrentCoordinates(),
                        command.possibleCoordinatesTo()));

        return Effect().persist(Moving.of(state));
    }

    protected Effect<WalkerState> onAlreadyMoving(
            @NonNull final WalkerState state,
            @NonNull final GetMoving command) {
        log.debug("{}[Path: {}][State: {}] on already moving", LABEL, actorPath(), state(state));

        return Effect().none();
    }

    protected Effect<WalkerState> onStop(
            @NonNull final WalkerState state,
            @NonNull final Stop command) {
        log.debug("{}[Path: {}][State: {}] on stand still", LABEL, actorPath(), state(state));

        return Effect().none().thenRun(_ -> startTimerToGetMoving(state));
    }

    private void startTimerToGetMoving(@NonNull final WalkerState state) {
        // Start a counter-delay (in the future, based on the walker velocity). When the timer reaches zero, it will
        // send to the dungeonRef the next movements it wants to take

        // Wait for some while (it could be the walker speed, the lower the value, the faster it moves) and then
        // send a self-command to start calculating where to go and to really move
        context.scheduleOnce(
                ofMillis(1000),
                context.getSelf(),
                new GetMoving(
                        // Calculate new coordinates
                        state.possibleCoordinatesTo()));
    }

}

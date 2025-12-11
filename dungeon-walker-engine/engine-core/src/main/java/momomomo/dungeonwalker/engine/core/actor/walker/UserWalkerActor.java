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
import momomomo.dungeonwalker.engine.core.actor.walker.command.Move;
import momomomo.dungeonwalker.engine.core.actor.walker.command.Stop;
import momomomo.dungeonwalker.engine.core.actor.walker.command.UpdateCoordinates;
import momomomo.dungeonwalker.engine.core.actor.walker.command.WalkerCommand;
import momomomo.dungeonwalker.engine.domain.model.coordinates.CoordinatesManager;
import momomomo.dungeonwalker.engine.domain.model.walker.moving.UserMovementStrategy;
import momomomo.dungeonwalker.engine.domain.model.walker.state.Moving;
import momomomo.dungeonwalker.engine.domain.model.walker.state.Sleeping;
import momomomo.dungeonwalker.engine.domain.model.walker.state.Stopped;
import momomomo.dungeonwalker.engine.domain.model.walker.state.WalkerState;

import java.util.List;

import static momomomo.dungeonwalker.engine.domain.model.walker.WalkerType.USER;

@Slf4j
public class UserWalkerActor extends WalkerActor {

    private static final String LABEL = "---> [ACTOR - User Walker]";

    public static final EntityTypeKey<WalkerCommand> ENTITY_TYPE_KEY =
            EntityTypeKey.create(WalkerCommand.class, "walkerRef-user-actor-type-key");

    private UserWalkerActor(
            @NonNull final ActorContext<WalkerCommand> context,
            @NonNull final PersistenceId persistenceId) {
        log.debug("{}[Path: {}][State: null] constructor", LABEL, context.getSelf().path().name());
        super(context, persistenceId);
    }

    public static Behavior<WalkerCommand> create(@NonNull final PersistenceId persistenceId) {
        log.debug("{}[persistenceId: {}] create", LABEL, persistenceId);
        return Behaviors.setup(context -> new UserWalkerActor(context, persistenceId));
    }

    @Override
    public WalkerState emptyState() {
        log.debug("{}[Path: {}][State: {}] empty value", LABEL, actorPath(), Sleeping.class.getSimpleName());
        return new Sleeping(entityId(), USER, new UserMovementStrategy());
    }

    @Override
    protected void setStandingStillStateCommands(
            @NonNull final CommandHandlerBuilderByState<WalkerCommand, Stopped, WalkerState> builder
    ) {
        builder
                .onCommand(UpdateCoordinates.class, this::onUpdateCoordinates)
                .onCommand(Move.class, this::onMove);
    }

    @Override
    protected void setOnTheMoveStateCommands(
            @NonNull CommandHandlerBuilderByState<WalkerCommand, Moving, WalkerState> builder
    ) {
        builder
                .onCommand(UpdateCoordinates.class, this::onUpdateCoordinates)
                .onCommand(Move.class, this::onAlreadyMoving)
                .onCommand(Stop.class, this::onStop);
    }

    @Override
    protected Effect<WalkerState> onUpdateCoordinates(
            @NonNull final WalkerState state,
            @NonNull final UpdateCoordinates command
    ) {
        log.debug("{}[Path: {}][State: {}] on update coordinates", LABEL, actorPath(), state(state));

        return Effect()
                .persist(Stopped.of(state.updateCoordinates(command.coordinates())))
                .thenRun(_ -> {
                    // TODO: Send to client the current coordinates
                });
    }

    protected Effect<WalkerState> onMove(
            @NonNull final WalkerState state,
            @NonNull final Move command
    ) {
        log.debug("{}[Path: {}][State: {}] on move", LABEL, actorPath(), state(state));

        return Effect()
                .persist(Moving.of(state))
                // Ask to be moved to the new coordinates
                .thenRun(_ -> dungeonEntityRef(state.getDungeonId())
                        .tell(new MoveWalker(
                                entityId(),
                                state.getType(),
                                state.getCurrentCoordinates(),
                                List.of(CoordinatesManager
                                        .of(state.getCurrentCoordinates())
                                        .move(command.to(), 1)
                                        .coordinates()))));
    }

    protected Effect<WalkerState> onAlreadyMoving(
            @NonNull final WalkerState state,
            @NonNull final Move command
    ) {
        log.debug("{}[Path: {}][State: {}] on already moving", LABEL, actorPath(), state(state));

        return Effect().none().thenRun(_ -> {
            // TODO: Send to client that no move command can be applied when the walker is moving
        });
    }

    protected Effect<WalkerState> onStop(
            @NonNull final WalkerState state,
            @NonNull final Stop command
    ) {
        log.debug("{}[Path: {}][State: {}] on stand still", LABEL, actorPath(), state(state));

        return Effect().persist(Stopped.of(state)).thenRun(_ -> {
            // TODO: Send to client that the walker is standing still
        });
    }

}

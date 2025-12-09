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
import momomomo.dungeonwalker.engine.core.actor.walker.command.StandStill;
import momomomo.dungeonwalker.engine.core.actor.walker.command.UpdateCoordinates;
import momomomo.dungeonwalker.engine.core.actor.walker.command.WalkerCommand;
import momomomo.dungeonwalker.engine.core.actor.walker.state.Awaken;
import momomomo.dungeonwalker.engine.core.actor.walker.state.OnTheMove;
import momomomo.dungeonwalker.engine.core.actor.walker.state.StandingStill;
import momomomo.dungeonwalker.engine.domain.model.coordinates.CoordinatesManager;
import momomomo.dungeonwalker.engine.domain.model.walker.Walker;
import momomomo.dungeonwalker.engine.domain.model.walker.moving.UserMovementStrategy;

import java.util.List;
import java.util.Objects;

@Slf4j
public class UserWalkerActor extends WalkerActor {

    public static final EntityTypeKey<WalkerCommand> ENTITY_TYPE_KEY =
            EntityTypeKey.create(WalkerCommand.class, "walkerRef-user-actor-type-key");

    private UserWalkerActor(
            @NonNull final ActorContext<WalkerCommand> context,
            @NonNull final PersistenceId persistenceId) {
        super(context, persistenceId);
    }

    public static Behavior<WalkerCommand> create(@NonNull final PersistenceId persistenceId) {
        log.debug("---> [ACTOR - User Walker][persistenceId: {}] create", persistenceId);
        return Behaviors.setup(context -> new UserWalkerActor(context, persistenceId));
    }

    @Override
    public Walker emptyState() {
        log.debug("---> [ACTOR - User Walker][path: {}] empty state", actorPath());
        return new Awaken(entityId(), new UserMovementStrategy());
    }

    @Override
    protected void setStandingStillStateCommands(
            @NonNull final CommandHandlerBuilderByState<WalkerCommand, StandingStill, Walker> builder
    ) {
        builder
                .onCommand(UpdateCoordinates.class, this::onUpdateCoordinates)
                .onCommand(Move.class, this::onMove);
    }

    @Override
    protected void setonTheMoveStateCommands(
            @NonNull CommandHandlerBuilderByState<WalkerCommand, OnTheMove, Walker> builder
    ) {
        builder
                .onCommand(UpdateCoordinates.class, this::onUpdateCoordinates)
                .onCommand(Move.class, this::onAlreadyMoving)
                .onCommand(StandStill.class, this::onStandStill);
    }

    @Override
    protected Effect<Walker> onUpdateCoordinates(
            @NonNull final Walker state,
            @NonNull final UpdateCoordinates command
    ) {
        log.debug("---> [ACTOR - User Walker][path: {}] on update coordinates", actorPath());

        final var effect = Objects.equals(state.getCurrentCoordinates(), command.coordinates()) ?
                Effect().none() :
                Effect().persist(StandingStill.of(state.updateCoordinates(command.coordinates())));

        return effect.thenRun(_ -> {
            // TODO: Send to client the current coordinates
        });
    }

    protected Effect<Walker> onMove(
            @NonNull final Walker state,
            @NonNull final Move command
    ) {
        log.debug("---> [ACTOR - User Walker][path: {}] on move", actorPath());

        // Ask to be moved to the new coordinates
        dungeonEntityRef(command.dungeonEntityId())
                .tell(new MoveWalker(
                        entityId(),
                        command.walkerType(),
                        state.getCurrentCoordinates(),
                        List.of(CoordinatesManager
                                .of(state.getCurrentCoordinates())
                                .move(command.to(), 1)
                                .coordinates())));

        return Effect().persist(OnTheMove.of(state));
    }

    protected Effect<Walker> onAlreadyMoving(
            @NonNull final Walker state,
            @NonNull final Move command
    ) {
        log.debug("---> [ACTOR - User Walker][path: {}] on already moving", actorPath());

        return Effect().none().thenRun(_ -> {
            // TODO: Send to client that no move command can be applied when the walker is moving
        });
    }

    protected Effect<Walker> onStandStill(
            @NonNull final Walker state,
            @NonNull final StandStill command
    ) {
        log.debug("---> [ACTOR - User Walker][path: {}] on stand still", actorPath());

        return Effect().none().thenRun(_ -> {
            // TODO: Send to client that the walker is standing still
        });
    }

}

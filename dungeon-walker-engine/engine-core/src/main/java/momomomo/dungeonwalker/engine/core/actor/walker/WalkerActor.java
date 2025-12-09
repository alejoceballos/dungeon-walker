package momomomo.dungeonwalker.engine.core.actor.walker;

import akka.actor.typed.javadsl.ActorContext;
import akka.cluster.sharding.typed.javadsl.ClusterSharding;
import akka.cluster.sharding.typed.javadsl.EntityRef;
import akka.persistence.typed.PersistenceId;
import akka.persistence.typed.state.javadsl.CommandHandler;
import akka.persistence.typed.state.javadsl.CommandHandlerBuilderByState;
import akka.persistence.typed.state.javadsl.DurableStateBehavior;
import akka.persistence.typed.state.javadsl.Effect;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.engine.core.actor.dungeon.DungeonActor;
import momomomo.dungeonwalker.engine.core.actor.dungeon.command.DungeonCommand;
import momomomo.dungeonwalker.engine.core.actor.dungeon.command.PlaceWalker;
import momomomo.dungeonwalker.engine.core.actor.walker.command.AskToEnterTheDungeon;
import momomomo.dungeonwalker.engine.core.actor.walker.command.UpdateCoordinates;
import momomomo.dungeonwalker.engine.core.actor.walker.command.WalkerCommand;
import momomomo.dungeonwalker.engine.core.actor.walker.state.Awaken;
import momomomo.dungeonwalker.engine.core.actor.walker.state.OnTheMove;
import momomomo.dungeonwalker.engine.core.actor.walker.state.StandingStill;
import momomomo.dungeonwalker.engine.core.actor.walker.state.WaitingToEnter;
import momomomo.dungeonwalker.engine.domain.model.walker.Walker;

@Slf4j
public abstract class WalkerActor extends DurableStateBehavior<WalkerCommand, Walker> {

    protected final ActorContext<WalkerCommand> context;

    private final ClusterSharding cluster;

    protected WalkerActor(
            @NonNull final ActorContext<WalkerCommand> context,
            @NonNull final PersistenceId persistenceId) {
        log.debug("---> [ACTOR - Walker][path: {}] constructor", context.getSelf().toString());
        this.cluster = ClusterSharding.get(context.getSystem());
        this.context = context;
        super(persistenceId);
    }

    @Override
    public CommandHandler<WalkerCommand, Walker> commandHandler() {
        log.debug("---> [ACTOR - Walker][path: {}] command handler", actorPath());

        final var builder = newCommandHandlerBuilder();

        builder.forStateType(Awaken.class)
                .onCommand(AskToEnterTheDungeon.class, this::onAskingToEnterTheDungeon);

        builder.forStateType(WaitingToEnter.class)
                .onCommand(UpdateCoordinates.class, this::onUpdateCoordinates);

        setStandingStillStateCommands(builder.forStateType(StandingStill.class));
        setonTheMoveStateCommands(builder.forStateType(OnTheMove.class));

        return builder.build();
    }

    protected abstract void setStandingStillStateCommands(
            @NonNull final CommandHandlerBuilderByState<WalkerCommand, StandingStill, Walker> builder);

    protected abstract void setonTheMoveStateCommands(
            @NonNull final CommandHandlerBuilderByState<WalkerCommand, OnTheMove, Walker> builder);

    protected Effect<Walker> onAskingToEnterTheDungeon(
            @NonNull final Walker state,
            @NonNull final AskToEnterTheDungeon command) {
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
                                        command.walkerType(),
                                        walker,
                                        command.placingStrategy())));
    }

    protected abstract Effect<Walker> onUpdateCoordinates(
            @NonNull final Walker state,
            @NonNull final UpdateCoordinates command);

    protected String actorPath() {
        return context.getSelf().path().toString();
    }

    protected String entityId() {
        return context.getSelf().path().name();
    }

    protected EntityRef<DungeonCommand> dungeonEntityRef(final String entityId) {
        return cluster.entityRefFor(DungeonActor.ENTITY_TYPE_KEY, entityId);
    }

}

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
import momomomo.dungeonwalker.engine.core.actor.dungeon.command.DungeonStateReply;
import momomomo.dungeonwalker.engine.core.actor.dungeon.command.DungeonStateRequest;
import momomomo.dungeonwalker.engine.core.actor.dungeon.command.MoveWalker;
import momomomo.dungeonwalker.engine.core.actor.dungeon.command.PlaceWalker;
import momomomo.dungeonwalker.engine.core.actor.dungeon.command.SetupDungeon;
import momomomo.dungeonwalker.engine.core.actor.walker.AutomatedWalkerActor;
import momomomo.dungeonwalker.engine.core.actor.walker.UserWalkerActor;
import momomomo.dungeonwalker.engine.core.actor.walker.command.Stop;
import momomomo.dungeonwalker.engine.core.actor.walker.command.UpdateCoordinates;
import momomomo.dungeonwalker.engine.core.actor.walker.command.WalkerCommand;
import momomomo.dungeonwalker.engine.domain.model.dungeon.state.DungeonState;
import momomomo.dungeonwalker.engine.domain.model.dungeon.state.InitializedDungeon;
import momomomo.dungeonwalker.engine.domain.model.dungeon.state.UninitializedDungeon;
import momomomo.dungeonwalker.engine.domain.model.walker.WalkerType;
import org.apache.commons.lang3.Strings;

import static java.util.Objects.isNull;

@Slf4j
public class DungeonActor extends DurableStateBehavior<DungeonCommand, DungeonState> {

    private static final String LABEL = "---> [ACTOR - Dungeon]";

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
        log.debug("{}[path: {}][State: null] constructor", LABEL, actorPath());
    }

    public static Behavior<DungeonCommand> create(final PersistenceId persistenceId) {
        log.debug("{}[persistenceId: {}] create", LABEL, persistenceId.toString());
        return Behaviors.setup(context -> new DungeonActor(context, persistenceId));
    }

    @Override
    public DungeonState emptyState() {
        log.debug("{}[path: {}][State: Uninitialized] empty value", LABEL, actorPath());
        return new UninitializedDungeon();
    }

    @Override
    public CommandHandler<DungeonCommand, DungeonState> commandHandler() {
        log.debug("{}[path: {}][State: ?] command handler", LABEL, actorPath());

        final var builder = newCommandHandlerBuilder();

        builder.forAnyState()
                .onCommand(DungeonStateRequest.class, this::onDungeonStateRequest);

        builder.forStateType(UninitializedDungeon.class)
                .onCommand(SetupDungeon.class, this::onSetupDungeon);

        builder.forStateType(InitializedDungeon.class)
                .onCommand(PlaceWalker.class, this::onPlaceWalker)
                .onCommand(MoveWalker.class, this::onMoveWalker);

        return builder.build();
    }

    private Effect<DungeonState> onDungeonStateRequest(
            final DungeonState state,
            final DungeonStateRequest command) {
        log.debug("{}[path: {}][State: {}] on dungeon state request", LABEL, actorPath(), state(state));
        return Effect()
                .none()
                .thenRun(_ -> command.replyTo().tell(new DungeonStateReply(state)));
    }

    private Effect<DungeonState> onSetupDungeon(
            final DungeonState state,
            final SetupDungeon command) {
        log.debug("{}[path: {}][State: {}] on setup dungeonRef", LABEL, actorPath(), state(state));

        command.dungeon().print();

        return Effect().persist(InitializedDungeon.of(command.dungeon()));
    }

    private Effect<DungeonState> onPlaceWalker(
            final DungeonState state,
            final PlaceWalker command) {
        log.debug("{}[path: {}][State: {}] on place walker", LABEL, actorPath(), state(state));

        final var coordinates = state.placeThing(
                command.placingStrategy(),
                command.walker());

        return Effect()
                .persist(state)
                .thenRun(_ -> walkerEntityRef(command.walkerType(), command.walkerEntityId())
                        .tell(new UpdateCoordinates(coordinates)));
    }

    private Effect<DungeonState> onMoveWalker(
            final DungeonState state,
            final MoveWalker command) {
        log.debug("{}[path: {}][State: {}] on move walker", LABEL, actorPath(), state(state));

        final var to = state.moveThing(
                command.from(),
                command.toPossibilities());

        state.print();

        return isNull(to) ?
                Effect()
                        .none()
                        .thenRun(_ ->
                                walkerEntityRef(command.walkerType(), command.walkerEntityId())
                                        .tell(new Stop())) :
                Effect()
                        .persist(state)
                        .thenRun(_ ->
                                walkerEntityRef(command.walkerType(), command.walkerEntityId())
                                        .tell(new UpdateCoordinates(to)));
    }

    private String actorPath() {
        return context.getSelf().path().name();
    }

    private String state(final DungeonState state) {
        return isNull(state) ? "null" : Strings.CS.removeEnd(state.getClass().getSimpleName(), "Dungeon");
    }

    private EntityRef<WalkerCommand> walkerEntityRef(final WalkerType walkerType, final String entityId) {
        return switch (walkerType) {
            case USER -> cluster.entityRefFor(UserWalkerActor.ENTITY_TYPE_KEY, entityId);
            case AUTOMATED -> cluster.entityRefFor(AutomatedWalkerActor.ENTITY_TYPE_KEY, entityId);
        };
    }

}

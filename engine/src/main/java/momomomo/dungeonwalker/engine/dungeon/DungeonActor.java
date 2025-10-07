package momomomo.dungeonwalker.engine.dungeon;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.domain.model.dungeon.Dungeon;
import momomomo.dungeonwalker.domain.model.dungeon.placing.DungeonPlacingStrategy;
import momomomo.dungeonwalker.domain.model.walker.Walker;
import momomomo.dungeonwalker.engine.dungeon.command.DungeonCommand;
import momomomo.dungeonwalker.engine.dungeon.command.MoveWalker;
import momomomo.dungeonwalker.engine.dungeon.command.PlaceWalker;
import momomomo.dungeonwalker.engine.dungeon.command.SetupDungeon;
import momomomo.dungeonwalker.engine.guardian.command.DungeonReady;
import momomomo.dungeonwalker.engine.guardian.command.EngineCommand;
import momomomo.dungeonwalker.engine.guardian.command.NotifyDungeonUpdated;
import momomomo.dungeonwalker.engine.walker.command.UpdateCoordinates;

@Slf4j
public class DungeonActor extends AbstractBehavior<DungeonCommand> {

    private Dungeon dungeon;
    private final ActorRef<EngineCommand> engineRef;
    private final DungeonPlacingStrategy placingStrategy;

    public DungeonActor(
            final ActorContext<DungeonCommand> context,
            final ActorRef<EngineCommand> engineRef,
            final DungeonPlacingStrategy placingStrategy) {
        super(context);
        this.engineRef = engineRef;
        this.placingStrategy = placingStrategy;
    }

    public static Behavior<DungeonCommand> create(
            final ActorRef<EngineCommand> engineRef,
            final DungeonPlacingStrategy placingStrategy) {
        log.debug("[ACTOR - Dungeon] create");
        return Behaviors.setup(context -> new DungeonActor(context, engineRef, placingStrategy));
    }

    @Override
    public Receive<DungeonCommand> createReceive() {
        log.debug("[ACTOR - Dungeon] create receive");
        return created();
    }

    public Receive<DungeonCommand> created() {
        log.debug("[ACTOR - Dungeon] created state");
        return newReceiveBuilder()
                .onMessage(SetupDungeon.class, this::onSetupDungeon)
                .build();
    }

    public Receive<DungeonCommand> ready() {
        log.debug("[ACTOR - Dungeon] ready state");
        return newReceiveBuilder()
                .onMessage(PlaceWalker.class, this::onPlaceWalker)
                .onMessage(MoveWalker.class, this::onMoveWalker)
                .build();
    }

    private Behavior<DungeonCommand> onSetupDungeon(final SetupDungeon command) {
        log.debug("[ACTOR - Dungeon] on setup dungeon");
        dungeon = command.dungeon();
        engineRef.tell(new DungeonReady(dungeon));
        return ready();
    }

    private Behavior<DungeonCommand> onPlaceWalker(final PlaceWalker command) {
        log.debug("[ACTOR - Dungeon] on place walker");
        final var coordinates = placingStrategy.placingCoordinates(dungeon);
        dungeon.at(coordinates).occupy(new Walker(command.walkerRef().path().name()));
        command.walkerRef().tell(new UpdateCoordinates(coordinates));
        engineRef.tell(new NotifyDungeonUpdated(dungeon));

        return Behaviors.same();
    }

    private Behavior<DungeonCommand> onMoveWalker(final MoveWalker command) {
        log.debug("[ACTOR - Dungeon] on move walker");

        command.toPossibilities()
                .stream()
                .filter(coordinates -> dungeon.at(coordinates).isFree())
                .findFirst()
                .ifPresent(coordinates -> {
                    // Get walker from its current position
                    final var walker = dungeon.at(command.from()).getOccupant();
                    // Put it into the new position
                    dungeon.at(coordinates).occupy(walker);
                    // Remove it from the old position
                    dungeon.at(command.from()).vacate();
                    // Tell the walker to update its coordinates
                    command.walkerRef().tell(new UpdateCoordinates(coordinates));
                    engineRef.tell(new NotifyDungeonUpdated(dungeon));
                });

        return Behaviors.same();
    }

}

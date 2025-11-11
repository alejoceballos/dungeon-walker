package momomomo.dungeonwalker.engine.core;

import akka.cluster.sharding.typed.javadsl.EntityRef;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.engine.core.actor.ClusterShardingManager;
import momomomo.dungeonwalker.engine.core.actor.dungeon.command.DungeonCommand;
import momomomo.dungeonwalker.engine.core.actor.dungeon.command.SetupDungeon;
import momomomo.dungeonwalker.engine.core.actor.walker.command.AskToEnterTheDungeon;
import momomomo.dungeonwalker.engine.domain.DungeonMaster;
import momomomo.dungeonwalker.engine.domain.model.coordinates.Coordinates;
import momomomo.dungeonwalker.engine.domain.model.dungeon.Dungeon;
import momomomo.dungeonwalker.engine.domain.model.dungeon.placing.DungeonPlacingStrategy;
import momomomo.dungeonwalker.engine.domain.model.dungeon.placing.SpiralStrategy;
import momomomo.dungeonwalker.engine.domain.model.walker.moving.SameDirectionOrRandomOtherwise;
import momomomo.dungeonwalker.engine.domain.model.walker.moving.WalkerMovingStrategy;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EngineManager implements DungeonMaster {

    // When having several dungeon levels, this ID will not be hard coded
    private static final String DUNGEON_ID = "LVL-01";

    // When having several dungeon levels, the placing strategy will depend on the level
    private static final DungeonPlacingStrategy PLACING_STRATEGY = new SpiralStrategy();

    // Currently, I only have one moving strategy. In the future this strategy will depend on the type of walker
    private static final WalkerMovingStrategy MOVING_STRATEGY = new SameDirectionOrRandomOtherwise();

    // In the future each level will have its own dungeon.
    private static final Dungeon DUNGEON = Dungeon.builder()
            .width(10)
            .height(10)
            .defaultSpawnLocation(Coordinates.of(5, 5))
            .build();

    private EntityRef<DungeonCommand> dungeonRef;

    private final ClusterShardingManager cluster;

    @PostConstruct
    public void init() {
        dungeonRef = cluster.getDungeonEntityRef(DUNGEON_ID);
        dungeonRef.tell(new SetupDungeon(DUNGEON));
    }

    @Override
    public void enterTheDungeon(final String playerId) {
        log.debug("[ENGINE - manager] Entering the dungeon: {}", playerId);
        cluster.getWalkerEntityRef(playerId)
                .tell(new AskToEnterTheDungeon(
                        dungeonRef,
                        PLACING_STRATEGY,
                        MOVING_STRATEGY));
    }

}

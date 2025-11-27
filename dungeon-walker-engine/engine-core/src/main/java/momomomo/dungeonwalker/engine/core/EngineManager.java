package momomomo.dungeonwalker.engine.core;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.engine.core.actor.ClusterShardingManager;
import momomomo.dungeonwalker.engine.core.actor.dungeon.command.SetupDungeon;
import momomomo.dungeonwalker.engine.core.actor.walker.command.AskToEnterTheDungeon;
import momomomo.dungeonwalker.engine.core.mapper.RawMapMapper;
import momomomo.dungeonwalker.engine.domain.DungeonMaster;
import momomomo.dungeonwalker.engine.domain.model.dungeon.placing.DungeonPlacingStrategy;
import momomomo.dungeonwalker.engine.domain.model.dungeon.placing.SpiralStrategy;
import momomomo.dungeonwalker.engine.domain.model.walker.moving.SameDirectionOrRandomOtherwise;
import momomomo.dungeonwalker.engine.domain.model.walker.moving.WalkerMovingStrategy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.IntStream;

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

    private final RawMapMapper mapper;

    private final ClusterShardingManager cluster;

    @PostConstruct
    public void init() throws IOException {
        final var rawMap = Files.readString(new ClassPathResource("dungeon/lvl-01.dgn").getFilePath());
        cluster
                .getDungeonEntityRef(DUNGEON_ID)
                .tell(new SetupDungeon(mapper.map(rawMap)));

        IntStream.of(1, 2, 3, 4).forEach(i -> cluster
                .getWalkerEntityRef(String.valueOf(i))
                .tell(new AskToEnterTheDungeon(
                        DUNGEON_ID,
                        PLACING_STRATEGY,
                        MOVING_STRATEGY)));
    }

    @Override
    public void enterTheDungeon(final String playerId) {
        log.debug("---> [ENGINE - manager] Entering the dungeon: {}", playerId);
        cluster.getWalkerEntityRef(playerId)
                .tell(new AskToEnterTheDungeon(
                        DUNGEON_ID,
                        PLACING_STRATEGY,
                        MOVING_STRATEGY));
    }

}

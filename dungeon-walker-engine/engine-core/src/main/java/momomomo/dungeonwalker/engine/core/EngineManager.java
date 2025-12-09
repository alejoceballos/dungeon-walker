package momomomo.dungeonwalker.engine.core;

import jakarta.annotation.PostConstruct;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.engine.core.actor.ClusterShardingManager;
import momomomo.dungeonwalker.engine.core.actor.dungeon.command.SetupDungeon;
import momomomo.dungeonwalker.engine.core.actor.walker.WalkerType;
import momomomo.dungeonwalker.engine.core.actor.walker.command.AskToEnterTheDungeon;
import momomomo.dungeonwalker.engine.core.actor.walker.command.Move;
import momomomo.dungeonwalker.engine.core.mapper.RawMapMapper;
import momomomo.dungeonwalker.engine.domain.DungeonMaster;
import momomomo.dungeonwalker.engine.domain.model.dungeon.placing.DungeonPlacingStrategy;
import momomomo.dungeonwalker.engine.domain.model.dungeon.placing.SpiralStrategy;
import momomomo.dungeonwalker.engine.domain.model.walker.moving.Direction;
import momomomo.dungeonwalker.engine.domain.model.walker.moving.SameDirectionOrRandomOtherwise;
import momomomo.dungeonwalker.engine.domain.model.walker.moving.UserMovementStrategy;
import momomomo.dungeonwalker.engine.domain.model.walker.moving.WalkerMovingStrategy;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static java.lang.System.lineSeparator;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;

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
        try (final var inputStream = getClass().getResourceAsStream("/dungeon/lvl-01.dgn")) {
            requireNonNull(inputStream);

            try (final var reader = new BufferedReader(new InputStreamReader(inputStream))) {
                final var rawMap = reader.lines().collect(joining(lineSeparator()));
                cluster.getDungeonEntityRef(DUNGEON_ID).tell(new SetupDungeon(mapper.map(rawMap)));
            }
        }

//        IntStream.of(1, 2, 3, 4).forEach(i -> cluster
//                .getWalkerEntityRef(String.valueOf(i))
//                .tell(new AskToEnterTheDungeon(
//                        DUNGEON_ID,
//                        PLACING_STRATEGY,
//                        MOVING_STRATEGY)));
    }

    @Override
    public void enterTheDungeon(@NonNull final String playerId) {
        log.debug("---> [ENGINE - manager] Entering the dungeon: {}", playerId);
        cluster.getUserWalkerEntityRef(playerId)
                .tell(new AskToEnterTheDungeon(
                        DUNGEON_ID,
                        WalkerType.USER,
                        PLACING_STRATEGY,
                        new UserMovementStrategy()));
    }

    @Override
    public void move(@NonNull final String playerId, @NonNull final Direction direction) {
        log.debug("---> [ENGINE - manager] Moving player {} to {}]", playerId, direction);
        cluster.getUserWalkerEntityRef(playerId).tell(new Move(DUNGEON_ID, WalkerType.USER, direction));
    }

}

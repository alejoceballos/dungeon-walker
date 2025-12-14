package momomomo.dungeonwalker.engine.core;

import jakarta.annotation.PostConstruct;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.engine.core.service.DungeonService;
import momomomo.dungeonwalker.engine.core.service.NpcService;
import momomomo.dungeonwalker.engine.core.service.PlayerService;
import momomomo.dungeonwalker.engine.domain.manager.DungeonManager;
import momomomo.dungeonwalker.engine.domain.manager.PlayerManager;
import momomomo.dungeonwalker.engine.domain.model.dungeon.Dungeon;
import momomomo.dungeonwalker.engine.domain.model.walker.moving.Direction;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.lang.String.join;

@Slf4j
@Component
@RequiredArgsConstructor
public class DungeonMaster implements DungeonManager, PlayerManager {

    private static final String LABEL = "---> [Dungeon Master]";

    private final DungeonService dungeonService;
    private final NpcService npcService;
    private final PlayerService playerService;

    @PostConstruct
    public void init() {
        log.debug("{} Init", LABEL);

        try (final var executor = Executors.newSingleThreadScheduledExecutor()) {
            executor.schedule(() -> {
                        log.debug("{} Calling dungeon level setup", LABEL);
                        dungeonService.setupLevel(1);
                    },
                    5L,
                    TimeUnit.SECONDS);

            executor.schedule(() -> {
                        final var npcIds = List.of("1", "2", "3", "4");
                        log.debug("{} Putting NPCs '{}' in the dungeon", LABEL, join("', '", npcIds));
                        npcIds.forEach(npcService::enterTheDungeon);
                    },
                    5L,
                    TimeUnit.SECONDS);
        }
    }

    @Override
    public void setupDungeon(final int level) {
        log.debug("{} Setting up dungeon level \"{}\"", LABEL, level);
        dungeonService.setupLevel(level);
    }

    @Override
    public Dungeon getDungeon() {
        return dungeonService.getDungeon(1);
    }

    @Override
    public void enterTheDungeon(@NonNull final String playerId) {
        log.debug("{} Player \"{}\" is entering the dungeon", LABEL, playerId);
        playerService.enterTheDungeon(playerId);
    }

    @Override
    public void move(@NonNull final String playerId, @NonNull final Direction direction) {
        log.debug("{} Moving player \"{}\" {}", LABEL, playerId, direction.name());
        playerService.move(playerId, direction);
    }

}

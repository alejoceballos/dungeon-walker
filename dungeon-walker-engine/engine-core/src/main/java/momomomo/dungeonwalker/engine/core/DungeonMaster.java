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
import momomomo.dungeonwalker.engine.domain.model.walker.moving.Direction;
import org.springframework.stereotype.Component;

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
        dungeonService.setupLevel(1);
    }

    @Override
    public void setupDungeon(final int level) {
        log.debug("{} Setting up dungeon level \"{}\"", LABEL, level);
        dungeonService.setupLevel(level);
    }

    @Override
    public void addNpcToDungeon(@NonNull final String npcId) {
        log.debug("{} Adding NPM \"{}\" to dungeon", LABEL, npcId);
        npcService.addToDungeon(npcId);
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

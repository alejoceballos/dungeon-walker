package momomomo.dungeonwalker.engine.core.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.engine.core.actor.ClusterShardingManager;
import momomomo.dungeonwalker.engine.core.actor.walker.command.AskToEnterTheDungeon;
import momomomo.dungeonwalker.engine.domain.model.dungeon.placing.DungeonPlacingStrategy;
import momomomo.dungeonwalker.engine.domain.model.dungeon.placing.SpiralStrategy;
import momomomo.dungeonwalker.engine.domain.model.walker.moving.UserMovementStrategy;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NpcService {

    private static final String LABEL = "---> [SERVICE - NPC]";

    // When having several dungeon levels, the placing strategy will depend on the level
    private static final DungeonPlacingStrategy PLACING_STRATEGY = new SpiralStrategy();

    private final IdentityService identityService;
    private final ClusterShardingManager cluster;

    public void addToDungeon(@NonNull final String npcId) {
        log.debug("{} Entering the dungeon: {}", LABEL, npcId);

        final var entityRef = cluster.getAutomatedWalkerEntityRef(npcId);

        // Ask if the NPC is already in a dungeon, if it does, ignore the code below

        // But if the player does not exist, then it must enter the dungeon
        entityRef.tell(new AskToEnterTheDungeon(
                identityService.dungeonId(1),
                PLACING_STRATEGY,
                new UserMovementStrategy()));
    }

}

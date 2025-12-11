package momomomo.dungeonwalker.engine.core.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.engine.core.actor.ClusterShardingManager;
import momomomo.dungeonwalker.engine.core.actor.walker.command.Move;
import momomomo.dungeonwalker.engine.core.actor.walker.command.WakeUp;
import momomomo.dungeonwalker.engine.domain.model.dungeon.placing.DungeonPlacingStrategy;
import momomomo.dungeonwalker.engine.domain.model.dungeon.placing.SpiralStrategy;
import momomomo.dungeonwalker.engine.domain.model.walker.moving.Direction;
import momomomo.dungeonwalker.engine.domain.model.walker.moving.UserMovementStrategy;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlayerService {

    private static final String LABEL = "---> [SERVICE - Player]";

    // When having several dungeon levels, the placing strategy will depend on the level
    private static final DungeonPlacingStrategy PLACING_STRATEGY = new SpiralStrategy();

    private final IdentityService identityService;
    private final ClusterShardingManager cluster;

    public void enterTheDungeon(@NonNull final String playerId) {
        log.debug("{} Entering the dungeon: {}", LABEL, playerId);

        final var entityRef = cluster.getUserWalkerEntityRef(playerId);

        // Ask if the player is already in a dungeon, if it does, ignore the code below

        // But if the player does not exist, then it must enter the dungeon
        entityRef.tell(new WakeUp(
                identityService.dungeonId(1),
                PLACING_STRATEGY,
                new UserMovementStrategy()));
    }

    public void move(
            @NonNull final String playerId,
            @NonNull final Direction direction
    ) {
        log.debug("{} Moving player {} to {}]", LABEL, playerId, direction);

        cluster.getUserWalkerEntityRef(playerId).tell(new Move(direction));
    }

}

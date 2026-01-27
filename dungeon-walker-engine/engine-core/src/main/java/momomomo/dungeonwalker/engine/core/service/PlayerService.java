package momomomo.dungeonwalker.engine.core.service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.engine.core.actor.ClusterShardingManager;
import momomomo.dungeonwalker.engine.core.actor.walker.command.Move;
import momomomo.dungeonwalker.engine.core.actor.walker.command.WalkerCommand;
import momomomo.dungeonwalker.engine.domain.model.walker.moving.Direction;
import momomomo.dungeonwalker.engine.domain.model.walker.moving.UserMovementStrategy;
import momomomo.dungeonwalker.engine.domain.model.walker.moving.WalkerMovementStrategy;
import momomomo.dungeonwalker.engine.domain.model.walker.state.WalkerState;
import org.apache.pekko.cluster.sharding.typed.javadsl.EntityRef;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PlayerService extends WalkerService {

    private static final String LABEL = "---> [SERVICE - Player]";

    public PlayerService(
            @NonNull final IdentityService identityService,
            @NonNull final ClusterShardingManager cluster
    ) {
        log.debug("{} Starting bean", LABEL);
        super(identityService, cluster);
    }

    public void move(
            @NonNull final String playerId,
            @NonNull final Direction direction
    ) {
        log.debug("{} Moving player {} to {}]", LABEL, playerId, direction);

        cluster.getUserWalkerEntityRef(playerId).tell(new Move(direction));
    }

    @Override
    protected boolean onEnterTheDungeonWhenInStoppedState(
            final EntityRef<WalkerCommand> walkerRef,
            final Class<? extends WalkerState> state) {
        return true;
    }

    @Override
    protected EntityRef<WalkerCommand> getWalkerRef(final String walkerId) {
        return cluster.getUserWalkerEntityRef(walkerId);
    }

    @Override
    protected WalkerMovementStrategy getMovementStrategy() {
        return new UserMovementStrategy();
    }

}

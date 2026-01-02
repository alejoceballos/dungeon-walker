package momomomo.dungeonwalker.engine.core.service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.engine.core.actor.ClusterShardingManager;
import momomomo.dungeonwalker.engine.core.actor.walker.command.RestartTimer;
import momomomo.dungeonwalker.engine.core.actor.walker.command.WalkerCommand;
import momomomo.dungeonwalker.engine.domain.model.walker.moving.SameDirectionOrRandomOtherwise;
import momomomo.dungeonwalker.engine.domain.model.walker.moving.WalkerMovementStrategy;
import momomomo.dungeonwalker.engine.domain.model.walker.state.Stopped;
import momomomo.dungeonwalker.engine.domain.model.walker.state.WalkerState;
import org.apache.pekko.cluster.sharding.typed.javadsl.EntityRef;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NpcService extends WalkerService {

    private static final String LABEL = "---> [SERVICE - NPC]";

    public NpcService(
            @NonNull final IdentityService identityService,
            @NonNull final ClusterShardingManager cluster
    ) {
        log.debug("{} Starting bean", LABEL);
        super(identityService, cluster);
    }

    @Override
    protected boolean onEnterTheDungeonWhenInStoppedState(
            final EntityRef<WalkerCommand> walkerRef,
            final Class<? extends WalkerState> state) {
        if (!Stopped.class.equals(state)) {
            return false;
        }

        log.debug("{} Restarting walker \"{}\" timer", LABEL, walkerRef.getEntityId());

        walkerRef.tell(new RestartTimer());

        return true;
    }

    @Override
    protected EntityRef<WalkerCommand> getWalkerRef(final String walkerId) {
        return cluster.getAutomatedWalkerEntityRef(walkerId);
    }

    @Override
    protected WalkerMovementStrategy getMovementStrategy() {
        return new SameDirectionOrRandomOtherwise();
    }

}

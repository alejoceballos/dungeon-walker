package momomomo.dungeonwalker.engine.core.actor.walker.state;

import momomomo.dungeonwalker.engine.domain.model.walker.Walker;
import momomomo.dungeonwalker.engine.domain.model.walker.moving.WalkerMovingStrategy;

public class WalkerState extends Walker {

    public WalkerState(String id, WalkerMovingStrategy movingStrategy) {
        super(id, movingStrategy);
    }

}

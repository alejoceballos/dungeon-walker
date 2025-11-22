package momomomo.dungeonwalker.engine.core.actor.walker.state;

import lombok.NonNull;
import momomomo.dungeonwalker.engine.domain.model.walker.Walker;
import momomomo.dungeonwalker.engine.domain.model.walker.moving.WalkerMovingStrategy;

public class Awaken extends Walker {

    public Awaken(@NonNull String id, @NonNull WalkerMovingStrategy movingStrategy) {
        super(id, movingStrategy);
    }

}

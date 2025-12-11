package momomomo.dungeonwalker.engine.core.actor.walker.state;

import lombok.NonNull;
import momomomo.dungeonwalker.engine.domain.model.walker.WalkerType;
import momomomo.dungeonwalker.engine.domain.model.walker.moving.WalkerMovingStrategy;

public class Awaken extends WalkerState {

    public Awaken(
            @NonNull final String id,
            @NonNull final WalkerType type,
            @NonNull final WalkerMovingStrategy movingStrategy) {
        super(id, type, movingStrategy, null);
    }

}

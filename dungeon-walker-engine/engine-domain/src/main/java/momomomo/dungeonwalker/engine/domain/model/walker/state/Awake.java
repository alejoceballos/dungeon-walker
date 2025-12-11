package momomomo.dungeonwalker.engine.domain.model.walker.state;

import lombok.NonNull;
import momomomo.dungeonwalker.engine.domain.model.walker.WalkerType;
import momomomo.dungeonwalker.engine.domain.model.walker.moving.WalkerMovingStrategy;

public class Awake extends WalkerState {

    public Awake(
            @NonNull final String id,
            @NonNull final WalkerType type,
            @NonNull final WalkerMovingStrategy movingStrategy,
            @NonNull final String dungeonId) {
        super(id, type, movingStrategy, dungeonId);
    }

}

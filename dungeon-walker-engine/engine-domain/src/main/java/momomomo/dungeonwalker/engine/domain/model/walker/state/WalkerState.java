package momomomo.dungeonwalker.engine.domain.model.walker.state;

import lombok.NonNull;
import momomomo.dungeonwalker.engine.domain.model.walker.Walker;
import momomomo.dungeonwalker.engine.domain.model.walker.WalkerType;
import momomomo.dungeonwalker.engine.domain.model.walker.moving.WalkerMovingStrategy;

public abstract class WalkerState extends Walker {

    public WalkerState(
            @NonNull final String id,
            @NonNull final WalkerType type,
            @NonNull final WalkerMovingStrategy movingStrategy,
            final String dungeonId) {
        super(id, type, movingStrategy, dungeonId);
    }

}

package momomomo.dungeonwalker.engine.domain.model.walker.state;

import lombok.NonNull;
import momomomo.dungeonwalker.engine.domain.model.walker.Walker;

public abstract class WalkerState extends Walker {

    protected WalkerState(
            @NonNull final String id,
            final String dungeonId
    ) {
        super(id, dungeonId);
    }

}

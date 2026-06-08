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

    protected static void copyCoordinates(@NonNull final Walker source, @NonNull final Walker target) {
        target.setPreviousCoordinates(source.getPreviousCoordinates());
        target.setCurrentCoordinates(source.getCurrentCoordinates());
    }

}

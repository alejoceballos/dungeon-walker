package momomomo.dungeonwalker.engine.domain.model.walker.state;

import lombok.NonNull;
import momomomo.dungeonwalker.engine.domain.model.walker.Walker;
import momomomo.dungeonwalker.engine.domain.model.walker.WalkerType;
import momomomo.dungeonwalker.engine.domain.model.walker.moving.WalkerMovementStrategy;

public class Moving extends WalkerState {

    public Moving(
            @NonNull final String id,
            @NonNull final WalkerType type,
            @NonNull final WalkerMovementStrategy movingStrategy,
            @NonNull final String dungeonId) {
        super(id, type, movingStrategy, dungeonId);
    }

    public static Moving of(@NonNull final Walker source) {
        final var target = new Moving(
                source.getId(),
                source.getType(),
                source.getMovingStrategy(),
                source.getDungeonId());

        target.setPreviousCoordinates(source.getPreviousCoordinates());
        target.setCurrentCoordinates(source.getCurrentCoordinates());

        return target;
    }

}

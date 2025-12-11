package momomomo.dungeonwalker.engine.core.actor.walker.state;

import lombok.NonNull;
import momomomo.dungeonwalker.engine.domain.model.walker.Walker;
import momomomo.dungeonwalker.engine.domain.model.walker.WalkerType;
import momomomo.dungeonwalker.engine.domain.model.walker.moving.WalkerMovingStrategy;

public class StandingStill extends WalkerState {

    public StandingStill(
            @NonNull final String id,
            @NonNull final WalkerType type,
            @NonNull final WalkerMovingStrategy movingStrategy,
            @NonNull final String dungeonId) {
        super(id, type, movingStrategy, dungeonId);
    }

    public static StandingStill of(@NonNull final Walker source) {
        final var target = new StandingStill(
                source.getId(),
                source.getType(),
                source.getMovingStrategy(),
                source.getDungeonId());

        target.setPreviousCoordinates(source.getPreviousCoordinates());
        target.setCurrentCoordinates(source.getCurrentCoordinates());

        return target;
    }

}

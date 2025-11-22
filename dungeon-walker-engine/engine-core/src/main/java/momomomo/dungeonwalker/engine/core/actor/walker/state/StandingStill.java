package momomomo.dungeonwalker.engine.core.actor.walker.state;

import momomomo.dungeonwalker.engine.domain.model.walker.Walker;
import momomomo.dungeonwalker.engine.domain.model.walker.moving.WalkerMovingStrategy;

public class StandingStill extends WalkerState {

    public StandingStill(String id, WalkerMovingStrategy movingStrategy) {
        super(id, movingStrategy);
    }

    public static StandingStill of(final Walker source) {
        final var target = new StandingStill(source.getId(), source.getMovingStrategy());

        target.setPreviousCoordinates(source.getPreviousCoordinates());
        target.setCurrentCoordinates(source.getCurrentCoordinates());

        return target;
    }

}

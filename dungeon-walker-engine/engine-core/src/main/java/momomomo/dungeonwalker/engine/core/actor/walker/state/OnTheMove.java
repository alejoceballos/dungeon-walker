package momomomo.dungeonwalker.engine.core.actor.walker.state;

import momomomo.dungeonwalker.engine.domain.model.walker.Walker;
import momomomo.dungeonwalker.engine.domain.model.walker.moving.WalkerMovingStrategy;

public class OnTheMove extends Walker {

    public OnTheMove(String id, WalkerMovingStrategy movingStrategy) {
        super(id, movingStrategy);
    }

    public static OnTheMove of(final Walker source) {
        final var target = new OnTheMove(source.getId(), source.getMovingStrategy());

        target.setPreviousCoordinates(source.getPreviousCoordinates());
        target.setCurrentCoordinates(source.getCurrentCoordinates());

        return target;
    }

}

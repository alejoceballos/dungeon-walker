package momomomo.dungeonwalker.engine.domain.model.walker;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import momomomo.dungeonwalker.engine.domain.model.coordinates.Coordinates;
import momomomo.dungeonwalker.engine.domain.model.dungeon.Thing;
import momomomo.dungeonwalker.engine.domain.model.walker.moving.WalkerMovingStrategy;

import java.util.List;

@RequiredArgsConstructor
public class Walker implements Thing {

    @Getter
    private final String id;

    @Getter
    private final WalkerMovingStrategy movingStrategy;

    @Getter
    @Setter
    private Coordinates previousCoordinates;

    @Getter
    @Setter
    private Coordinates currentCoordinates;

    public List<Coordinates> possibleCoordinatesTo() {
        return movingStrategy.nextCoordinates(previousCoordinates, currentCoordinates);
    }

    public Walker updateCoordinates(final Coordinates coordinates) {
        this.previousCoordinates = this.currentCoordinates;
        this.currentCoordinates = coordinates;
        return this;
    }

}

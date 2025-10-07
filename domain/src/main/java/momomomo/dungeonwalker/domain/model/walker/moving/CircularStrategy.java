package momomomo.dungeonwalker.domain.model.walker.moving;

import lombok.NonNull;
import momomomo.dungeonwalker.domain.model.coordinates.Coordinates;
import momomomo.dungeonwalker.domain.model.coordinates.CoordinatesManager;

import java.util.ArrayList;
import java.util.List;

public class CircularStrategy implements WalkerMovingStrategy {

    @Override
    public List<Coordinates> nextCoordinates(@NonNull final Coordinates currentCoordinates) {
        final var nextCoords = new ArrayList<Coordinates>();
        final var coordinatesManager = CoordinatesManager.of(currentCoordinates);

        nextCoords.add(coordinatesManager.moveEast(1).coordinates());
        nextCoords.add(coordinatesManager.moveSouth(1).coordinates());
        nextCoords.add(coordinatesManager.moveWest(1).coordinates());
        nextCoords.add(coordinatesManager.moveWest(1).coordinates());
        nextCoords.add(coordinatesManager.moveNorth(1).coordinates());
        nextCoords.add(coordinatesManager.moveNorth(1).coordinates());
        nextCoords.add(coordinatesManager.moveEast(1).coordinates());
        nextCoords.add(coordinatesManager.moveEast(1).coordinates());

        return nextCoords;
    }

}

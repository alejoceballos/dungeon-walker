package momomomo.dungeonwalker.engine.domain.model.walker.moving;

import lombok.NonNull;
import momomomo.dungeonwalker.engine.domain.model.coordinates.Coordinates;
import momomomo.dungeonwalker.engine.domain.model.coordinates.CoordinatesManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

import static java.util.Objects.nonNull;
import static momomomo.dungeonwalker.engine.domain.model.walker.moving.Direction.EAST;
import static momomomo.dungeonwalker.engine.domain.model.walker.moving.Direction.NORTH;
import static momomomo.dungeonwalker.engine.domain.model.walker.moving.Direction.NORTHEAST;
import static momomomo.dungeonwalker.engine.domain.model.walker.moving.Direction.NORTHWEST;
import static momomomo.dungeonwalker.engine.domain.model.walker.moving.Direction.SOUTH;
import static momomomo.dungeonwalker.engine.domain.model.walker.moving.Direction.SOUTHEAST;
import static momomomo.dungeonwalker.engine.domain.model.walker.moving.Direction.SOUTHWEST;
import static momomomo.dungeonwalker.engine.domain.model.walker.moving.Direction.WEST;

public class SameDirectionOrRandomOtherwise implements WalkerMovingStrategy {

    @Override
    public List<Coordinates> nextCoordinates(
            final Coordinates previousCoordinates,
            @NonNull final Coordinates currentCoordinates) {
        final var nextCoordinates = new ArrayList<Coordinates>();

        final List<Direction> possibleDirections = new ArrayList<>(
                List.of(NORTH, NORTHEAST, EAST, SOUTHEAST, SOUTH, SOUTHWEST, WEST, NORTHWEST));

        final Map<Direction, Consumer<Void>> possibleMovements = new HashMap<>(
                Map.of(
                        EAST, (_) -> nextCoordinates.add(
                                CoordinatesManager
                                        .of(currentCoordinates)
                                        .moveEast(1)
                                        .coordinates()),
                        SOUTHEAST, (_) -> nextCoordinates.add(
                                CoordinatesManager
                                        .of(currentCoordinates)
                                        .moveSoutheast(1)
                                        .coordinates()),
                        SOUTH, (_) -> nextCoordinates.add(
                                CoordinatesManager
                                        .of(currentCoordinates)
                                        .moveSouth(1)
                                        .coordinates()),
                        SOUTHWEST, (_) -> nextCoordinates.add(
                                CoordinatesManager
                                        .of(currentCoordinates)
                                        .moveSouthwest(1)
                                        .coordinates()),
                        WEST, (_) -> nextCoordinates.add(
                                CoordinatesManager
                                        .of(currentCoordinates)
                                        .moveWest(1)
                                        .coordinates()),
                        NORTHWEST, (_) -> nextCoordinates.add(
                                CoordinatesManager
                                        .of(currentCoordinates)
                                        .moveNorthwest(1)
                                        .coordinates()),
                        NORTH, (_) -> nextCoordinates.add(
                                CoordinatesManager
                                        .of(currentCoordinates)
                                        .moveNorth(1)
                                        .coordinates()),
                        NORTHEAST, (_) -> nextCoordinates.add(
                                CoordinatesManager
                                        .of(currentCoordinates)
                                        .moveNortheast(1)
                                        .coordinates())));

        calculateFirstCoordinateAndUpdatePossibleDirections(
                possibleDirections,
                previousCoordinates,
                currentCoordinates,
                possibleMovements
        );

        randomizeNextCoordinatesUpdatingPossibleDirections(
                possibleDirections,
                possibleMovements);

        return nextCoordinates;
    }

    private static void randomizeNextCoordinatesUpdatingPossibleDirections(
            final List<Direction> possibleDirections,
            final Map<Direction, Consumer<Void>> possibleMovements) {
        final var random = new Random();

        while (!possibleDirections.isEmpty()) {
            final var index = random.nextInt(0, possibleDirections.size());
            possibleMovements.get(possibleDirections.get(index)).accept(null);
            possibleDirections.remove(index);
        }
    }

    private static void calculateFirstCoordinateAndUpdatePossibleDirections(
            final List<Direction> possibleDirections,
            final Coordinates previousCoordinates,
            final Coordinates currentCoordinates,
            final Map<Direction, Consumer<Void>> possibleMovements) {

        var deltaX = 0;
        var deltaY = 0;

        if (nonNull(previousCoordinates)) {
            deltaX = currentCoordinates.x() - previousCoordinates.x();
            deltaY = currentCoordinates.y() - previousCoordinates.y();
        }

        if (deltaX == 0 && deltaY == 0) {
            possibleMovements.get(EAST).accept(null);
            possibleDirections.remove(EAST);
        } else if (deltaX == 0 && deltaY < 0) {
            possibleMovements.get(NORTH).accept(null);
            possibleDirections.remove(NORTH);
        } else if (deltaX > 0 && deltaY < 0) {
            possibleMovements.get(NORTHEAST).accept(null);
            possibleDirections.remove(NORTHEAST);
        } else if (deltaX > 0 && deltaY == 0) {
            possibleMovements.get(EAST).accept(null);
            possibleDirections.remove(EAST);
        } else if (deltaX > 0) { // && deltaY > 0
            possibleMovements.get(SOUTHEAST).accept(null);
            possibleDirections.remove(SOUTHEAST);
        } else if (deltaX == 0) { // && deltaY > 0
            possibleMovements.get(SOUTH).accept(null);
            possibleDirections.remove(SOUTH);
        } else if (deltaY > 0) { // && deltaX < 0
            possibleMovements.get(SOUTHWEST).accept(null);
            possibleDirections.remove(SOUTHWEST);
        } else if (deltaY == 0) {  // && deltaX < 0
            possibleMovements.get(WEST).accept(null);
            possibleDirections.remove(WEST);
        } else { // deltaX < 0 && deltaY < 0
            possibleMovements.get(NORTHWEST).accept(null);
            possibleDirections.remove(NORTHWEST);
        }
    }

}

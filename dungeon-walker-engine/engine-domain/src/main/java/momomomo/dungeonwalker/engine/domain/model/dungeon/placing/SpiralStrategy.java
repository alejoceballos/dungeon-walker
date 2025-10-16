package momomomo.dungeonwalker.engine.domain.model.dungeon.placing;

import momomomo.dungeonwalker.engine.domain.model.coordinates.Coordinates;
import momomomo.dungeonwalker.engine.domain.model.dungeon.Dungeon;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * Checks for available empty spots in the map in a spiral search strategy, circling around the center and expanding
 * until it finds it.
 */
public class SpiralStrategy implements DungeonPlacingStrategy {

    public SpiralStrategy() {
    }

    @Override
    public Coordinates placingCoordinates(final Dungeon dungeon) {
        final var strategy = new StrategyState(dungeon.getDefaultSpawnLocation());
        Coordinates availableCoordinates = null;

        while (isNull(availableCoordinates)) {
            availableCoordinates = getAvailableCoordinates(
                    dungeon,
                    dungeon.getDefaultSpawnLocation(),
                    strategy);
        }

        return availableCoordinates;
    }

    private Coordinates getAvailableCoordinates(
            final Dungeon dungeon,
            final Coordinates coordinates,
            final StrategyState strategy) {

        final var cell = dungeon.at(coordinates);

        if (nonNull(cell) && cell.isFree()) {
            return coordinates;

        } else if (dungeon.isEdge(coordinates)) {
            return null;
        }

        return getAvailableCoordinates(
                dungeon,
                strategy.nextCoordinate(),
                strategy);
    }

    static class StrategyState {

        private static final List<Coordinates> BASE_COORDINATES_AROUND_CENTER = List.of(
                new Coordinates(1, 0),
                new Coordinates(1, 1),
                new Coordinates(0, 1),
                new Coordinates(-1, 1),
                new Coordinates(-1, 0),
                new Coordinates(-1, -1),
                new Coordinates(0, 1),
                new Coordinates(1, 1));

        private final Coordinates center;

        private Iterator<Coordinates> currentCoordinates = Collections.emptyIterator();
        private int currentRadius = -1;

        StrategyState(final Coordinates center) {
            this.center = center;
        }

        Coordinates nextCoordinate() {
            if (!currentCoordinates.hasNext()) {
                currentRadius++;
                currentCoordinates = BASE_COORDINATES_AROUND_CENTER.iterator();
            }

            final Coordinates currentSpiralCoordinates = currentCoordinates.next();

            return new Coordinates(
                    currentSpiralCoordinates.x() + center.x() + currentRadius,
                    currentSpiralCoordinates.y() + center.y() + currentRadius);
        }

    }

}

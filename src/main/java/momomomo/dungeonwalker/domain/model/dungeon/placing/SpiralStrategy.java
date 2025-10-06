package momomomo.dungeonwalker.domain.model.dungeon.placing;

import momomomo.dungeonwalker.domain.model.coordinates.Coordinates;
import momomomo.dungeonwalker.domain.model.dungeon.Dungeon;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Checks for available empty spots in the map in a spiral search strategy, circling around the center and expanding
 * until it finds it.
 */
public class SpiralStrategy implements DungeonPlacingStrategy {

    public SpiralStrategy() {
    }

    @Override
    public Coordinates placingCoordinates(final Dungeon dungeon) {
        return getAvailableCoordinates(dungeon, dungeon.getDefaultSpawnLocation());
    }

    private Coordinates getAvailableCoordinates(
            final Dungeon dungeon,
            final Coordinates coordinates) {
        final var strategy = new StrategyState(dungeon.getDefaultSpawnLocation());

        if (dungeon.at(coordinates).isFree()) {
            return coordinates;
        }

        return getAvailableCoordinates(dungeon, strategy.nextCoordinate());
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

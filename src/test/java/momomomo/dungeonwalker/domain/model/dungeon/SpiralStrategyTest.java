package momomomo.dungeonwalker.domain.model.dungeon;

import momomomo.dungeonwalker.domain.model.coordinates.Coordinates;
import momomomo.dungeonwalker.domain.model.dungeon.placing.DungeonPlacingStrategy;
import momomomo.dungeonwalker.domain.model.dungeon.placing.SpiralStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static momomomo.dungeonwalker.domain.model.coordinates.Coordinates.Axis.X;
import static org.assertj.core.api.Assertions.assertThat;

class SpiralStrategyTest {

    private Dungeon dungeon;
    private DungeonPlacingStrategy strategy;

    @BeforeEach
    void setUp() {
        dungeon = new Dungeon(10, 10, new Coordinates(5, 5));

        for (int x = 0; x < dungeon.getWidth(); x++) {
            dungeon.add(new Cell(new Coordinates(x, 0), new Wall()));
            dungeon.add(new Cell(new Coordinates(x, dungeon.getHeight() - 1), new Wall()));
        }

        for (int y = 1; y < dungeon.getHeight() - 1; y++) {
            dungeon.add(new Cell(new Coordinates(0, y), new Wall()));
            dungeon.add(new Cell(new Coordinates(dungeon.getWidth() - 1, y), new Wall()));
        }

        strategy = new SpiralStrategy();
    }

    @Test
    void testFirstTryIsASpot() {
        assertThat(strategy.placingCoordinates(dungeon)).isEqualTo(dungeon.getDefaultSpawnLocation());
    }

    @Test
    void testCenterIsOccupied() {
        dungeon.add(new Cell(dungeon.getDefaultSpawnLocation(), new Wall()));

        final var expected = new Coordinates(6, 5);

        assertThat(strategy.placingCoordinates(dungeon)).isEqualTo(expected);
    }

    @Test
    void testWillGetTheFirstSouthEastSpot() {
        dungeon.add(new Cell(dungeon.getDefaultSpawnLocation(), new Wall()));
        dungeon.add(new Cell(dungeon.getDefaultSpawnLocation().adjust(1, X), new Wall()));

        final var expected = new Coordinates(6, 6);

        assertThat(strategy.placingCoordinates(dungeon)).isEqualTo(expected);
    }

}

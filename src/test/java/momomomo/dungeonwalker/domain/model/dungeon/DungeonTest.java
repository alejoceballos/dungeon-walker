package momomomo.dungeonwalker.domain.model.dungeon;

import momomomo.dungeonwalker.domain.model.coordinates.Coordinates;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class DungeonTest {

    @Test
    public void testDungeonCreation() {
        final var dungeon = new Dungeon(10, 10, new Coordinates(0, 0));
        assertNotNull(dungeon);
    }

}
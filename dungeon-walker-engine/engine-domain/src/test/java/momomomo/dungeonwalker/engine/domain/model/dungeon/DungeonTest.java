package momomomo.dungeonwalker.engine.domain.model.dungeon;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import momomomo.dungeonwalker.engine.domain.model.coordinates.Coordinates;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DungeonTest {

    private Dungeon dungeon;

    @BeforeEach
    void setUp() {
        dungeon = Dungeon
                .builder()
                .width(5)
                .height(5)
                .defaultSpawnLocation(Coordinates.of(2, 2))
                .build();

        for (int x = 0; x < dungeon.width; x++) {
            for (int y = 0; y < dungeon.height; y++) {
                final var cell = new Cell(Coordinates.of(x, y));

                if (x == 0 || y == 0 || x == dungeon.width - 1 || y == dungeon.height - 1) {
                    cell.occupy(new Wall());
                }

                dungeon.add(cell);
            }
        }
    }

    @Test
    void testSerialization() throws JsonProcessingException {
        assertThat(dungeon.cells).hasSize(25);
        assertThat(dungeon.cells.values().stream()
                .filter(cell -> cell.getOccupant() instanceof Wall)
                .count()).isEqualTo(16);

        final var serialized = new ObjectMapper().writeValueAsString(dungeon);
        System.out.println(serialized);
        final var deserialized = new ObjectMapper().readValue(serialized, Dungeon.class);
        System.out.println(deserialized);
    }

}
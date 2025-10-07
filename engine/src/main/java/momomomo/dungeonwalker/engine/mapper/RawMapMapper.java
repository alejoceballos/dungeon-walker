package momomomo.dungeonwalker.engine.mapper;

import momomomo.dungeonwalker.domain.mapper.DungeonMapper;
import momomomo.dungeonwalker.domain.model.coordinates.Coordinates;
import momomomo.dungeonwalker.domain.model.dungeon.Cell;
import momomomo.dungeonwalker.domain.model.dungeon.Dungeon;
import momomomo.dungeonwalker.domain.model.dungeon.Wall;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
public class RawMapMapper implements DungeonMapper<String> {

    public Dungeon map(final String rawMap, final Coordinates coordinates) {
        Dungeon dungeon = null;

        final var lines = rawMap.split(System.lineSeparator());
        final var ySize = lines.length;

        for (int y = 0; y < ySize; y++) {
            final var line = lines[y].split(",");
            final var xSize = line.length;

            if (isNull(dungeon)) {
                dungeon = new Dungeon(xSize, ySize, coordinates);
            }

            for (int x = 0; x < xSize; x++) {
                final var thing = "W".equals(line[x]) ? new Wall() : null;
                dungeon.add(new Cell(Coordinates.of(x, y), thing));
            }
        }

        return dungeon;
    }

}

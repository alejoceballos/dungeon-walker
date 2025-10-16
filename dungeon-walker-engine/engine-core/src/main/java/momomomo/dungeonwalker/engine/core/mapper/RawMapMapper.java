package momomomo.dungeonwalker.engine.core.mapper;

import momomomo.dungeonwalker.engine.domain.mapper.DungeonMapper;
import momomomo.dungeonwalker.engine.domain.model.coordinates.Coordinates;
import momomomo.dungeonwalker.engine.domain.model.dungeon.Cell;
import momomomo.dungeonwalker.engine.domain.model.dungeon.Dungeon;
import momomomo.dungeonwalker.engine.domain.model.dungeon.Wall;
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

package momomomo.dungeonwalker.engine.core.mapper;

import jakarta.annotation.Nonnull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import momomomo.dungeonwalker.engine.core.service.IdentityService;
import momomomo.dungeonwalker.engine.domain.mapper.DungeonMapper;
import momomomo.dungeonwalker.engine.domain.mapper.MappingDungeonException;
import momomomo.dungeonwalker.engine.domain.model.coordinates.Coordinates;
import momomomo.dungeonwalker.engine.domain.model.dungeon.Cell;
import momomomo.dungeonwalker.engine.domain.model.dungeon.Dungeon;
import momomomo.dungeonwalker.engine.domain.model.dungeon.Wall;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
public class RawMapMapper implements DungeonMapper<String> {

    private final IdentityService identityService;

    public @Nonnull Dungeon map(
            final int dungeonLevel,
            @NonNull final String rawMap
    ) {
        Dungeon dungeon = null;

        final var lines = rawMap.split(System.lineSeparator());
        final var ySize = lines.length;

        int stablishedXSize = -1;

        for (int y = 0; y < ySize; y++) {
            final var line = lines[y].split(",");

            final var xSize = line.length;

            if (stablishedXSize == -1) {
                stablishedXSize = xSize;

            } else if (stablishedXSize != xSize) {
                throw new MappingDungeonException("Invalid raw map! All lines must have the same size");
            }

            if (isNull(dungeon)) {
                dungeon = Dungeon.builder()
                        .width(xSize)
                        .height(ySize)
                        .build();
            }

            for (int x = 0; x < xSize; x++) {
                if ("X".equals(line[x])) {
                    dungeon.setDefaultSpawnLocation(Coordinates.of(x, y));
                }

                final var thing = "W".equals(line[x]) ?
                        new Wall(identityService.dungeonId(dungeonLevel)) :
                        null;

                dungeon.add(new Cell(Coordinates.of(x, y), thing));
            }
        }

        if (isNull(dungeon)) {
            throw new MappingDungeonException("Invalid raw map. Unable to create a dungeon. Check the template");
        }

        return dungeon;
    }

}

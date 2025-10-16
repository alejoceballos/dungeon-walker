package momomomo.dungeonwalker.engine.domain.model.dungeon;

import lombok.Getter;
import lombok.NonNull;
import momomomo.dungeonwalker.engine.domain.model.coordinates.Coordinates;

import java.util.HashMap;
import java.util.Map;

public class Dungeon {

    @Getter
    private final int width;

    @Getter
    private final int height;

    private final Map<Coordinates, Cell> cells;

    @Getter
    private final Coordinates defaultSpawnLocation;

    public Dungeon(
            final int width,
            final int height,
            @NonNull Coordinates defaultSpawnLocation
    ) {
        this.width = width;
        this.height = height;
        this.defaultSpawnLocation = defaultSpawnLocation;

        this.cells = new HashMap<>(width * height);
    }

    public void add(@NonNull final Cell cell) {
        cells.put(cell.getCoordinates(), cell);
    }

    public Cell at(@NonNull final Coordinates coordinates) {
        return cells.get(coordinates);
    }

    public boolean isEdge(@NonNull final Coordinates coordinates) {
        return coordinates.x() == 0 || coordinates.y() == 0 || coordinates.x() == width || coordinates.y() == height;
    }

}

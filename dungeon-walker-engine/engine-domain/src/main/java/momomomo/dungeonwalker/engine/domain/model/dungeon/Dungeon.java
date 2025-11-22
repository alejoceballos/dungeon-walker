package momomomo.dungeonwalker.engine.domain.model.dungeon;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import momomomo.dungeonwalker.engine.domain.model.coordinates.Coordinates;
import momomomo.dungeonwalker.engine.domain.model.walker.Walker;
import momomomo.dungeonwalker.engine.domain.serializer.CoordinatesDeserializer;
import momomomo.dungeonwalker.engine.domain.serializer.CoordinatesSerializer;

import java.util.HashMap;
import java.util.Map;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Dungeon {

    @Getter
    protected int width;

    @Getter
    protected int height;

    @Getter
    @Setter
    protected Coordinates defaultSpawnLocation;

    @Getter
    @JsonSerialize(keyUsing = CoordinatesSerializer.class)
    @JsonDeserialize(keyUsing = CoordinatesDeserializer.class)
    protected final Map<Coordinates, Cell> cells = new HashMap<>();

    public void add(@NonNull final Cell cell) {
        cells.put(cell.getCoordinates(), cell);
    }

    public Cell at(@NonNull final Coordinates coordinates) {
        return cells.get(coordinates);
    }

    public boolean isEdge(@NonNull final Coordinates coordinates) {
        return coordinates.x() == 0 || coordinates.y() == 0 || coordinates.x() == width || coordinates.y() == height;
    }

    public void print() {
        System.out.println();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                final String cell = switch (at(Coordinates.of(x, y)).getOccupant()) {
                    case Wall _ -> "#";
                    case Walker _ -> "X";
                    case null, default -> " ";
                };

                System.out.print(cell);
            }

            System.out.println();
        }
    }

}

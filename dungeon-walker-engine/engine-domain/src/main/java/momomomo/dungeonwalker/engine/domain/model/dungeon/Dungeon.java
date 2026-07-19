package momomomo.dungeonwalker.engine.domain.model.dungeon;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.engine.domain.model.coordinates.Coordinates;
import momomomo.dungeonwalker.engine.domain.model.coordinates.serializer.CoordinatesDeserializer;
import momomomo.dungeonwalker.engine.domain.model.coordinates.serializer.CoordinatesSerializer;
import momomomo.dungeonwalker.engine.domain.model.dungeon.placing.DungeonPlacingStrategy;
import momomomo.dungeonwalker.engine.domain.model.walker.Walker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toMap;

@Slf4j
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Dungeon {

    private static final String LABEL = "---> [DUNGEON]";

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

    @Getter
    protected final Map<String, Coordinates> walkersPositions = new HashMap<>();

    public Map<String, Coordinates> getDungeonState() {
        return cells
                .entrySet()
                .stream()
                .filter(entry -> !entry.getValue().isFree())
                .collect(toMap(
                        entry -> entry.getValue().getOccupant().getId(),
                        Map.Entry::getKey));
    }

    public void add(@NonNull final Cell cell) {
        log.debug("{}[Add cell] {}", LABEL, logCell(cell));
        cells.put(cell.getCoordinates(), cell);

        if (cell.isFree()) {
            return;
        }

        updatePositions(cell.getOccupant(), cell.getCoordinates());
    }

    public Cell at(@NonNull final Coordinates coordinates) {
        return cells.get(coordinates);
    }

    public boolean isEdge(@NonNull final Coordinates coordinates) {
        return coordinates.x() == -1 || coordinates.y() == -1 || coordinates.x() == width || coordinates.y() == height;
    }

    public Coordinates placeWalker(@NonNull final DungeonPlacingStrategy placingStrategy, @NonNull final Walker walker) {
        log.debug("{}[Place walker] {}", LABEL, walker.getId());

        if (walkersPositions.containsKey(walker.getId())) {
            log.debug("{}[Walker already placed] {}", LABEL, logKeyValue(walker, walkersPositions.get(walker.getId())));
            return walkersPositions.get(walker.getId());
        }

        final var coordinates = placingStrategy.placingCoordinates(this);
        at(coordinates).occupy(walker);

        log.debug("{}[Walker placed] {}", LABEL, logKeyValue(walker, coordinates));

        updatePositions(walker, coordinates);

        return coordinates;
    }

    public Coordinates removeWalker(@NonNull final String walkerId) {
        log.debug("{}[Remove walker] {}", LABEL, walkerId);

        if (!walkersPositions.containsKey(walkerId)) {
            log.debug("{}[Walker not found] {}", LABEL, walkerId);
            return null;
        }

        final var coordinates = walkersPositions.remove(walkerId);
        final var formerOccupant = at(coordinates).vacate();

        log.debug("{}[Walker removed] {}", LABEL, logKeyValue(formerOccupant, coordinates));

        return coordinates;
    }

    public Coordinates moveThing(@NonNull final Coordinates from, @NonNull final List<Coordinates> toPossibilities) {
        final var to = toPossibilities
                .stream()
                .filter(coordinates -> at(coordinates).isFree())
                .findFirst()
                .orElse(null);

        if (isNull(to)) {
            return null;
        }

        // Get walker from its current position
        final var thing = at(from).getOccupant();
        // Put it into the new position
        at(to).occupy(thing);
        // Remove it from the old position
        at(from).vacate();

        updatePositions(thing, to);

        return to;
    }

    public String print() {
        final var builder = new StringBuilder();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                final String cell = switch (at(Coordinates.of(x, y)).getOccupant()) {
                    case Wall _ -> "#";
                    case final Walker walker -> walker.getId().substring(0, 1);
                    case null, default -> " ";
                };

                builder.append(cell);
            }

            builder.append("\n");
        }

        return builder.toString();
    }

    private void updatePositions(@NonNull final Thing thing, @NonNull final Coordinates coordinates) {
        if (thing instanceof final Walker walker) {
            log.debug("{}[Update Positions] Put into \"walkersPositions\" map. {}", LABEL, logKeyValue(walker, coordinates));
            walkersPositions.put(walker.getId(), coordinates);
        }
    }

    private static String logCell(final Cell cell) {
        final var id = cell.getOccupant() == null ? "<empty>" : cell.getOccupant().getId();
        return "[%s: (%d, %d)]".formatted(id, cell.getCoordinates().x(), cell.getCoordinates().y());
    }

    private static String logKeyValue(final Thing thing, final Coordinates coordinates) {
        return "Key: %s. Value: %s".formatted(thing.getId(), coordinates);
    }

}

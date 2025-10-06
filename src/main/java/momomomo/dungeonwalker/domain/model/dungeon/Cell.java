package momomomo.dungeonwalker.domain.model.dungeon;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import momomomo.dungeonwalker.domain.model.coordinates.Coordinates;

import static java.util.Objects.isNull;

@Builder
public class Cell {

    @Getter
    private final Coordinates coordinates;

    @Getter
    private Thing occupant;

    public Cell(@NonNull Coordinates coordinates, Thing occupant) {
        this.coordinates = coordinates;
        this.occupant = occupant;
    }

    public boolean isFree() {
        return isNull(occupant);
    }

    public void occupy(final Thing occupant) {
        if (this.occupant != null) {
            throw new CellException("Cell at %s already occupied".formatted(coordinates));
        }

        this.occupant = occupant;
    }

    public void vacate() {
        if (this.occupant == null) {
            throw new CellException("Cell at %s has no occupant".formatted(coordinates));
        }

        this.occupant = null;
    }

}

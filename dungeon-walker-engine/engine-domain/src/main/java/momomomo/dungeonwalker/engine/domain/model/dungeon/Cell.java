package momomomo.dungeonwalker.engine.domain.model.dungeon;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import momomomo.dungeonwalker.engine.domain.model.coordinates.Coordinates;
import momomomo.dungeonwalker.engine.domain.model.walker.Walker;

import static java.util.Objects.isNull;

@NoArgsConstructor
@AllArgsConstructor
public class Cell {

    @Getter
    private Coordinates coordinates;

    @Getter
    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.PROPERTY,
            property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = Wall.class, name = "wall"),
            @JsonSubTypes.Type(value = Walker.class, name = "walker")
    })
    private Thing occupant;

    public Cell(final Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    @JsonIgnore
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

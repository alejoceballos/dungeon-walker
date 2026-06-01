package momomomo.dungeonwalker.engine.domain.model.walker;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import momomomo.dungeonwalker.engine.domain.model.coordinates.Coordinates;
import momomomo.dungeonwalker.engine.domain.model.dungeon.Thing;

public class Walker implements Thing {

    public Walker(
            @NonNull final String id,
            final String dungeonId
    ) {
        this.id = id;
        this.dungeonId = dungeonId;
    }

    @Getter
    @NonNull
    private final String id;

    @Getter
    @Setter
    private String dungeonId;

    @Getter
    @Setter
    private Coordinates previousCoordinates;

    @Getter
    @Setter
    private Coordinates currentCoordinates;

    public @NonNull Walker updateCoordinates(@NonNull final Coordinates coordinates) {
        this.previousCoordinates = this.currentCoordinates;
        this.currentCoordinates = coordinates;
        return this;
    }

}

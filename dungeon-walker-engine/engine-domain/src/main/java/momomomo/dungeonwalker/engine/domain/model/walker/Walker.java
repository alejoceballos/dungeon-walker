package momomomo.dungeonwalker.engine.domain.model.walker;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import momomomo.dungeonwalker.engine.domain.model.coordinates.Coordinates;
import momomomo.dungeonwalker.engine.domain.model.dungeon.Thing;
import momomomo.dungeonwalker.engine.domain.model.walker.moving.WalkerMovementStrategy;

import java.util.List;

public class Walker implements Thing {

    public Walker(
            @NonNull final String id,
            @NonNull final WalkerType type,
            @NonNull final WalkerMovementStrategy movingStrategy,
            final String dungeonId) {
        this.id = id;
        this.type = type;
        this.movingStrategy = movingStrategy;
        this.dungeonId = dungeonId;
    }

    @Getter
    @NonNull
    private final String id;

    @Getter
    @NonNull
    private final WalkerType type;

    @Getter
    @NonNull
    private final WalkerMovementStrategy movingStrategy;

    @Getter
    @Setter
    private String dungeonId;

    @Getter
    @Setter
    private Coordinates previousCoordinates;

    @Getter
    @Setter
    private Coordinates currentCoordinates;

    public @NonNull List<Coordinates> possibleCoordinatesTo() {
        return movingStrategy.nextCoordinates(previousCoordinates, currentCoordinates);
    }

    public @NonNull Walker updateCoordinates(@NonNull final Coordinates coordinates) {
        this.previousCoordinates = this.currentCoordinates;
        this.currentCoordinates = coordinates;
        return this;
    }

}

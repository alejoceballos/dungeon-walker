package momomomo.dungeonwalker.engine.domain.model.walker.moving;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import momomomo.dungeonwalker.engine.domain.model.coordinates.Coordinates;

import java.util.List;

public interface WalkerMovingStrategy {

    @Nonnull
    List<Coordinates> nextCoordinates(
            @Nullable Coordinates previousCoordinates,
            @Nonnull Coordinates currentCoordinates);

}

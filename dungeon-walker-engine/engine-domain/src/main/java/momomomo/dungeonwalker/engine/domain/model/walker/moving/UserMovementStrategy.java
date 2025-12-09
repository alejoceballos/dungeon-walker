package momomomo.dungeonwalker.engine.domain.model.walker.moving;

import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.engine.domain.model.coordinates.Coordinates;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;

@Slf4j
public class UserMovementStrategy implements WalkerMovingStrategy {

    @Override
    public @Nonnull List<Coordinates> nextCoordinates(
            @Nullable final Coordinates previousCoordinates,
            @NonNull final Coordinates currentCoordinates) {
        log.warn("---> [MOVING STRATEGY - User] It will always return an empty list. User movements are not automatic, " +
                "but controlled by user commands.");
        return List.of();
    }

}

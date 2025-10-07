package momomomo.dungeonwalker.engine.walker.command;

import lombok.NonNull;
import momomomo.dungeonwalker.domain.model.coordinates.Coordinates;

public record UpdateCoordinates(@NonNull Coordinates coordinates) implements WalkerCommand {
}

package momomomo.dungeonwalker.engine.core.actor.walker.command;

import lombok.NonNull;
import momomomo.dungeonwalker.engine.domain.model.coordinates.Coordinates;

public record UpdateCoordinates(@NonNull Coordinates coordinates) implements WalkerCommand {
}

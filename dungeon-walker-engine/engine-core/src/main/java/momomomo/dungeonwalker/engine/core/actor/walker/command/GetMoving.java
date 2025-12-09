package momomomo.dungeonwalker.engine.core.actor.walker.command;

import lombok.NonNull;
import momomomo.dungeonwalker.engine.core.actor.walker.WalkerType;
import momomomo.dungeonwalker.engine.domain.model.coordinates.Coordinates;

import java.util.List;

public record GetMoving(
        @NonNull String dungeonEntityId,
        @NonNull WalkerType walkerType,
        @NonNull List<Coordinates> possibleCoordinatesTo
) implements WalkerCommand {
}

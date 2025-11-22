package momomomo.dungeonwalker.engine.core.actor.dungeon.command;

import lombok.NonNull;
import momomomo.dungeonwalker.engine.domain.model.coordinates.Coordinates;

import java.util.List;

public record MoveWalker(
        @NonNull String walkerEntityId,
        @NonNull Coordinates from,
        @NonNull List<Coordinates> toPossibilities
) implements DungeonCommand {
}

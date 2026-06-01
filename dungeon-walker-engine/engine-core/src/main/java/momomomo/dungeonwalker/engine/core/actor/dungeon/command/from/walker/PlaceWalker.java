package momomomo.dungeonwalker.engine.core.actor.dungeon.command.from.walker;

import lombok.NonNull;
import momomomo.dungeonwalker.engine.core.actor.dungeon.command.DungeonCommand;
import momomomo.dungeonwalker.engine.domain.model.dungeon.placing.DungeonPlacingStrategy;
import momomomo.dungeonwalker.engine.domain.model.walker.Walker;

public record PlaceWalker(
        @NonNull String walkerEntityId,
        @NonNull Walker walker,
        @NonNull DungeonPlacingStrategy placingStrategy
) implements DungeonCommand {
}

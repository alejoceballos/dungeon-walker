package momomomo.dungeonwalker.engine.core.actor.dungeon.command;

import lombok.NonNull;
import momomomo.dungeonwalker.engine.domain.model.dungeon.placing.DungeonPlacingStrategy;
import momomomo.dungeonwalker.engine.domain.model.walker.Walker;
import momomomo.dungeonwalker.engine.domain.model.walker.WalkerType;

public record PlaceWalker(
        @NonNull String walkerEntityId,
        @NonNull WalkerType walkerType,
        @NonNull Walker walker,
        @NonNull DungeonPlacingStrategy placingStrategy
) implements DungeonCommand {
}

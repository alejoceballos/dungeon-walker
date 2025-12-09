package momomomo.dungeonwalker.engine.core.actor.walker.command;

import lombok.NonNull;
import momomomo.dungeonwalker.engine.core.actor.walker.WalkerType;
import momomomo.dungeonwalker.engine.domain.model.dungeon.placing.DungeonPlacingStrategy;
import momomomo.dungeonwalker.engine.domain.model.walker.moving.WalkerMovingStrategy;

public record AskToEnterTheDungeon(
        @NonNull String dungeonEntityId,
        @NonNull WalkerType walkerType,
        @NonNull DungeonPlacingStrategy placingStrategy,
        @NonNull WalkerMovingStrategy movingStrategy
) implements WalkerCommand {
}

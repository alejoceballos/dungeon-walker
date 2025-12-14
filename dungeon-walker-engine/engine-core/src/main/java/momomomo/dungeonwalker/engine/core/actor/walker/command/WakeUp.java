package momomomo.dungeonwalker.engine.core.actor.walker.command;

import lombok.NonNull;
import momomomo.dungeonwalker.engine.domain.model.dungeon.placing.DungeonPlacingStrategy;
import momomomo.dungeonwalker.engine.domain.model.walker.moving.WalkerMovementStrategy;

public record WakeUp(
        @NonNull String dungeonEntityId,
        @NonNull DungeonPlacingStrategy placingStrategy,
        @NonNull WalkerMovementStrategy movingStrategy
) implements WalkerCommand {
}

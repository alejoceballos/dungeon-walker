package momomomo.dungeonwalker.engine.core.actor.walker.command;

import lombok.NonNull;
import momomomo.dungeonwalker.engine.core.actor.walker.WalkerType;
import momomomo.dungeonwalker.engine.domain.model.walker.moving.Direction;

public record Move(
        @NonNull String dungeonEntityId,
        @NonNull WalkerType walkerType,
        @NonNull Direction to
) implements WalkerCommand {
}

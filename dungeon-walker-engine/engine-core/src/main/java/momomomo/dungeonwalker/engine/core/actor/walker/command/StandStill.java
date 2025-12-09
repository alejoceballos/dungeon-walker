package momomomo.dungeonwalker.engine.core.actor.walker.command;

import lombok.NonNull;
import momomomo.dungeonwalker.engine.core.actor.walker.WalkerType;

public record StandStill(
        @NonNull String dungeonEntityId,
        @NonNull WalkerType walkerType
) implements WalkerCommand {
}

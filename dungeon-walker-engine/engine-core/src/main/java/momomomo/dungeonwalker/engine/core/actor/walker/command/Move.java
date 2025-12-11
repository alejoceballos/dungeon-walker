package momomomo.dungeonwalker.engine.core.actor.walker.command;

import lombok.NonNull;
import momomomo.dungeonwalker.engine.domain.model.walker.moving.Direction;

public record Move(@NonNull Direction to) implements WalkerCommand {
}

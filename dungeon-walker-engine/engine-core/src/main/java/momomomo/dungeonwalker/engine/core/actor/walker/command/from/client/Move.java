package momomomo.dungeonwalker.engine.core.actor.walker.command.from.client;

import lombok.NonNull;
import momomomo.dungeonwalker.engine.core.actor.walker.command.WalkerCommand;
import momomomo.dungeonwalker.engine.domain.model.walker.moving.Direction;

public record Move(@NonNull Direction to) implements WalkerCommand {
}

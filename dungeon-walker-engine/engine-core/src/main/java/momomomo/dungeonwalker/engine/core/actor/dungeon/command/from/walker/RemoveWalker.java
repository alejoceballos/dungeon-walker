package momomomo.dungeonwalker.engine.core.actor.dungeon.command.from.walker;

import lombok.NonNull;
import momomomo.dungeonwalker.engine.core.actor.dungeon.command.DungeonCommand;

public record RemoveWalker(@NonNull String walkerEntityId) implements DungeonCommand {
}

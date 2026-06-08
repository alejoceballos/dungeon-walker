package momomomo.dungeonwalker.engine.core.actor.dungeon.command.to;

import lombok.NonNull;
import momomomo.dungeonwalker.engine.core.actor.dungeon.command.DungeonCommand;
import momomomo.dungeonwalker.engine.domain.model.dungeon.Dungeon;

public record DungeonStateReply(@NonNull Dungeon value) implements DungeonCommand {
}

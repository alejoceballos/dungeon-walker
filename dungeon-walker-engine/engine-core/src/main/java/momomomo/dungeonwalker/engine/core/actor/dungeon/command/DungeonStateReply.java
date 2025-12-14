package momomomo.dungeonwalker.engine.core.actor.dungeon.command;

import lombok.NonNull;
import momomomo.dungeonwalker.engine.domain.model.dungeon.Dungeon;

public record DungeonStateReply(@NonNull Dungeon value) implements DungeonCommand {
}

package momomomo.dungeonwalker.engine.core.actor.dungeon.command;

import lombok.NonNull;
import momomomo.dungeonwalker.engine.core.actor.dungeon.state.DungeonState;

public record DungeonStateReply(@NonNull Class<? extends DungeonState> value) implements DungeonCommand {
}

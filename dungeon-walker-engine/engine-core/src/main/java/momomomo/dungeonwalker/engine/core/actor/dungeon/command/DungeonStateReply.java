package momomomo.dungeonwalker.engine.core.actor.dungeon.command;

import lombok.NonNull;
import momomomo.dungeonwalker.engine.domain.model.dungeon.state.DungeonState;

public record DungeonStateReply(@NonNull Class<? extends DungeonState> value) implements DungeonCommand {
}

package momomomo.dungeonwalker.engine.core.dungeon.command;

import lombok.NonNull;
import momomomo.dungeonwalker.engine.domain.model.dungeon.Dungeon;

public record SetupDungeon(@NonNull Dungeon dungeon) implements DungeonCommand {
}

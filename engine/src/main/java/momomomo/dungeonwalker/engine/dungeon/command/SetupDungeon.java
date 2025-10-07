package momomomo.dungeonwalker.engine.dungeon.command;

import lombok.NonNull;
import momomomo.dungeonwalker.domain.model.dungeon.Dungeon;

public record SetupDungeon(@NonNull Dungeon dungeon) implements DungeonCommand {
}

package momomomo.dungeonwalker.engine.core.actor.dungeon.command;

import lombok.NonNull;
import momomomo.dungeonwalker.engine.domain.model.dungeon.Dungeon;

public record SetupDungeon(@NonNull Dungeon dungeon) implements DungeonCommand {
}

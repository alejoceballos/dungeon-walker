package momomomo.dungeonwalker.engine.core.actor.dungeon.command.from.setup;

import lombok.NonNull;
import momomomo.dungeonwalker.engine.core.actor.dungeon.command.DungeonCommand;
import momomomo.dungeonwalker.engine.domain.model.dungeon.Dungeon;

public record SetupDungeon(@NonNull Dungeon dungeon) implements DungeonCommand {
}

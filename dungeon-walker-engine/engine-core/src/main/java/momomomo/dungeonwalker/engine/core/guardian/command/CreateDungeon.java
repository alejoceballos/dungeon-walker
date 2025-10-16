package momomomo.dungeonwalker.engine.core.guardian.command;

import lombok.NonNull;
import momomomo.dungeonwalker.engine.domain.model.dungeon.Dungeon;

public record CreateDungeon(@NonNull Dungeon dungeon) implements EngineCommand {
}

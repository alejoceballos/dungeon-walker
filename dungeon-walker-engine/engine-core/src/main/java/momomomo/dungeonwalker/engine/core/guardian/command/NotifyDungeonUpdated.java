package momomomo.dungeonwalker.engine.core.guardian.command;

import lombok.NonNull;
import momomomo.dungeonwalker.engine.domain.model.dungeon.Dungeon;

public record NotifyDungeonUpdated(@NonNull Dungeon dungeon) implements EngineCommand {
}

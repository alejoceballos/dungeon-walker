package momomomo.dungeonwalker.engine.guardian.command;

import lombok.NonNull;
import momomomo.dungeonwalker.domain.model.dungeon.Dungeon;

public record NotifyDungeonUpdated(@NonNull Dungeon dungeon) implements EngineCommand {
}

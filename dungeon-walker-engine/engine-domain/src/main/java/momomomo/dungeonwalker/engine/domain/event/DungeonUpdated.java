package momomomo.dungeonwalker.engine.domain.event;

import lombok.NonNull;
import momomomo.dungeonwalker.engine.domain.model.dungeon.Dungeon;

public record DungeonUpdated(@NonNull Dungeon dungeon) implements DungeonEvent {
}

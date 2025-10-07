package momomomo.dungeonwalker.domain.event;

import lombok.NonNull;
import momomomo.dungeonwalker.domain.model.dungeon.Dungeon;

public record DungeonUpdated(@NonNull Dungeon dungeon) implements DungeonEvent {
}

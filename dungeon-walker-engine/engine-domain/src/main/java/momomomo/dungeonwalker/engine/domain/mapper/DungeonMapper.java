package momomomo.dungeonwalker.engine.domain.mapper;

import lombok.NonNull;
import momomomo.dungeonwalker.engine.domain.model.dungeon.Dungeon;

public interface DungeonMapper<T> {

    @NonNull Dungeon map(int dungeonLevel, @NonNull T original);

}

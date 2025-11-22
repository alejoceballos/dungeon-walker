package momomomo.dungeonwalker.engine.domain.mapper;

import momomomo.dungeonwalker.engine.domain.model.dungeon.Dungeon;

public interface DungeonMapper<T> {

    Dungeon map(T original);

}

package momomomo.dungeonwalker.domain.mapper;

import momomomo.dungeonwalker.domain.model.coordinates.Coordinates;
import momomomo.dungeonwalker.domain.model.dungeon.Dungeon;

public interface DungeonMapper<T> {

    Dungeon map(T original, Coordinates coordinates);

}

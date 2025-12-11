package momomomo.dungeonwalker.engine.domain.mapper;

import jakarta.annotation.Nonnull;
import momomomo.dungeonwalker.engine.domain.model.dungeon.Dungeon;

public interface DungeonMapper<T> {

    @Nonnull Dungeon map(int dungeonLevel, @Nonnull T original);

}

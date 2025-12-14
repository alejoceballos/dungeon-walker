package momomomo.dungeonwalker.engine.domain.manager;

import momomomo.dungeonwalker.engine.domain.model.dungeon.Dungeon;

public interface DungeonManager {

    void setupDungeon(final int level);

    Dungeon getDungeon();

}

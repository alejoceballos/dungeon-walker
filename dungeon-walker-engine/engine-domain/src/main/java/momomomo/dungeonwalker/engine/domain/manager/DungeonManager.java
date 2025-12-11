package momomomo.dungeonwalker.engine.domain.manager;

import jakarta.annotation.Nonnull;

public interface DungeonManager {

    void setupDungeon(final int level);

    void addNpcToDungeon(@Nonnull final String npcId);

}

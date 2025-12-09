package momomomo.dungeonwalker.engine.domain;

import jakarta.annotation.Nonnull;
import momomomo.dungeonwalker.engine.domain.model.walker.moving.Direction;

public interface DungeonMaster {

    void enterTheDungeon(@Nonnull final String playerId);

    void move(@Nonnull final String playerId, @Nonnull final Direction direction);

}

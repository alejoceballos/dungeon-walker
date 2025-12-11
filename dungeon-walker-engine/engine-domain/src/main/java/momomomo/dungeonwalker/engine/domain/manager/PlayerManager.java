package momomomo.dungeonwalker.engine.domain.manager;

import jakarta.annotation.Nonnull;
import momomomo.dungeonwalker.engine.domain.model.walker.moving.Direction;

public interface PlayerManager {

    void enterTheDungeon(@Nonnull final String playerId);

    void move(@Nonnull final String playerId, @Nonnull final Direction direction);

}

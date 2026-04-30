package momomomo.dungeonwalker.engine.domain.manager;

import lombok.NonNull;
import momomomo.dungeonwalker.engine.domain.model.walker.moving.Direction;

public interface PlayerManager {

    void enterTheDungeon(@NonNull final String playerId);

    void move(@NonNull final String playerId, @NonNull final Direction direction);

}

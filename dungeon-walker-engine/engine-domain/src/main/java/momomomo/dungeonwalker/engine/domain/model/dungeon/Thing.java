package momomomo.dungeonwalker.engine.domain.model.dungeon;

import lombok.NonNull;

public interface Thing {

    @NonNull
    String getId();

    @NonNull
    String getDungeonId();

}

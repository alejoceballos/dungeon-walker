package momomomo.dungeonwalker.engine.domain.model.dungeon;

import lombok.NonNull;

public record Wall(@NonNull String dungeonId) implements Thing {

    @Override
    public @NonNull String getDungeonId() {
        return dungeonId;
    }

}

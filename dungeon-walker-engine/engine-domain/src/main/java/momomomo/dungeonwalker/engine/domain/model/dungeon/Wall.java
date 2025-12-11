package momomomo.dungeonwalker.engine.domain.model.dungeon;

import jakarta.annotation.Nonnull;
import lombok.NonNull;

public record Wall(@NonNull String dungeonId) implements Thing {

    @Override
    public @Nonnull String getDungeonId() {
        return dungeonId;
    }

}

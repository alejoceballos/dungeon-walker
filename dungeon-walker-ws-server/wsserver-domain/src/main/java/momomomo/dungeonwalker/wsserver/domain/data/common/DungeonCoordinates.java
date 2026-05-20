package momomomo.dungeonwalker.wsserver.domain.data.common;

import lombok.NonNull;

public record DungeonCoordinates(int x, int y) {

    @Override
    public @NonNull String toString() {
        return "(" + x + ", " + y + ")";
    }

}

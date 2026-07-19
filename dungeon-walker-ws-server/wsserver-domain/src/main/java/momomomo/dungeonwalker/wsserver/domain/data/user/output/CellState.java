package momomomo.dungeonwalker.wsserver.domain.data.user.output;

import lombok.NonNull;
import momomomo.dungeonwalker.wsserver.domain.data.common.DungeonCoordinates;

public record CellState(
        String id,
        @NonNull DungeonCoordinates coordinates
) implements OutputData {
}

package momomomo.dungeonwalker.wsserver.domain.data.user.output;

import lombok.NonNull;
import momomomo.dungeonwalker.wsserver.domain.data.common.DungeonCoordinates;

import java.util.Map;

public record DungeonState(
        int height,
        int width,
        @NonNull Map<String, DungeonCoordinates> coordinates
) implements OutputData {
}

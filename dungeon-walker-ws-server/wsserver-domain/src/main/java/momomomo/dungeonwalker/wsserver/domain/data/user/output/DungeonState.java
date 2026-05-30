package momomomo.dungeonwalker.wsserver.domain.data.user.output;

import lombok.NonNull;
import momomomo.dungeonwalker.wsserver.domain.data.common.DungeonCoordinates;

import java.util.Map;

public record DungeonState(@NonNull Map<String, DungeonCoordinates> values) implements OutputData {
}

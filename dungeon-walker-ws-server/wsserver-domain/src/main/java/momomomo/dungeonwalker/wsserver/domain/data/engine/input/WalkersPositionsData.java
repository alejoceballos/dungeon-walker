package momomomo.dungeonwalker.wsserver.domain.data.engine.input;

import lombok.NonNull;
import momomomo.dungeonwalker.wsserver.domain.data.common.DungeonCoordinates;

import java.util.Map;

/**
 * A broadcast message informing the coordinates of each walker in the map.
 *
 * @param walkerPositions a map with the coordinates of each walker in the map, indexed by the walker id.
 */
public record WalkersPositionsData(
        @NonNull Map<String, DungeonCoordinates> walkerPositions
) implements BroadcastMessage {
}

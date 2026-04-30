package momomomo.dungeonwalker.wsserver.domain.input.engine;

import lombok.NonNull;

import java.util.Map;

/**
 * A broadcast message informing the coordinates of each walker in the map.
 *
 * @param walkerPositions a map with the coordinates of each walker in the map, indexed by the walker id.
 */
public record WalkersPositionsData(
        @NonNull Map<String, CoordinatesData> walkerPositions
) implements BroadcastMessage {
}

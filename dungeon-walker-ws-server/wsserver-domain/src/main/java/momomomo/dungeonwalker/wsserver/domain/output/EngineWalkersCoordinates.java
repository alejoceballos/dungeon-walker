package momomomo.dungeonwalker.wsserver.domain.output;

import lombok.NonNull;

import java.util.Map;

public record EngineWalkersCoordinates(@NonNull Map<String, EngineCoordinates> values) implements OutputData {
}

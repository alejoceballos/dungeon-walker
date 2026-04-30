package momomomo.dungeonwalker.wsserver.core.actor.connection.command.from.engine;

import lombok.NonNull;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.ConnectionCommand;
import momomomo.dungeonwalker.wsserver.domain.input.engine.CoordinatesData;
import momomomo.dungeonwalker.wsserver.domain.output.EngineCoordinates;
import momomomo.dungeonwalker.wsserver.domain.output.EngineWalkersCoordinates;

import java.util.Map;

import static java.util.stream.Collectors.toMap;

public record SendWalkersPositionToClient(
        @NonNull Map<String, CoordinatesData> walkerPositions
) implements ConnectionCommand {

    public EngineWalkersCoordinates toDomain() {
        return new EngineWalkersCoordinates(
                walkerPositions
                        .entrySet()
                        .stream()
                        .collect(toMap(
                                Map.Entry::getKey,
                                entry -> new EngineCoordinates(
                                        entry.getValue().x(),
                                        entry.getValue().y()))));
    }

}

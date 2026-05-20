package momomomo.dungeonwalker.wsserver.core.actor.connection.command.from.engine;

import lombok.NonNull;
import momomomo.dungeonwalker.contract.engine.EngineMessageProto.EngineMessage;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.ConnectionCommand;
import momomomo.dungeonwalker.wsserver.domain.data.client.output.DungeonState;
import momomomo.dungeonwalker.wsserver.domain.data.common.DungeonCoordinates;

import java.util.Map;

import static java.util.stream.Collectors.toMap;

public record EnteredTheDungeon(
        @NonNull String clientId,
        @NonNull Map<String, DungeonCoordinates> dungeonState
) implements ConnectionCommand {

    public DungeonState toDomain() {
        return new DungeonState(Map.copyOf(dungeonState));
    }

    public static EnteredTheDungeon of(@NonNull final EngineMessage message) {
        return new EnteredTheDungeon(
                message.getEnteredDungeon().getClientId(),
                message.getEnteredDungeon()
                        .getCoordinatesMap()
                        .entrySet()
                        .stream()
                        .collect(toMap(
                                Map.Entry::getKey,
                                entry -> new DungeonCoordinates(
                                        entry.getValue().getX(),
                                        entry.getValue().getY()))));
    }

}

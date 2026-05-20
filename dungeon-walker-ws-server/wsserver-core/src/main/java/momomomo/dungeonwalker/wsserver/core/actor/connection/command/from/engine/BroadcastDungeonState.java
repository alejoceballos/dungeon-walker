package momomomo.dungeonwalker.wsserver.core.actor.connection.command.from.engine;

import lombok.NonNull;
import momomomo.dungeonwalker.contract.engine.EngineMessageProto.EngineMessage;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.ConnectionCommand;
import momomomo.dungeonwalker.wsserver.domain.data.client.output.DungeonState;
import momomomo.dungeonwalker.wsserver.domain.data.common.DungeonCoordinates;

import java.util.Map;
import java.util.stream.Collectors;

public record BroadcastDungeonState(
        @NonNull Map<String, DungeonCoordinates> dungeonState
) implements ConnectionCommand {

    public DungeonState toDomain() {
        return new DungeonState(Map.copyOf(dungeonState));
    }

    public static BroadcastDungeonState of(@NonNull final EngineMessage message) {
        return new BroadcastDungeonState(message
                .getEnteredDungeon()
                .getCoordinatesMap()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> new DungeonCoordinates(
                                entry.getValue().getX(),
                                entry.getValue().getY()))));
    }

}

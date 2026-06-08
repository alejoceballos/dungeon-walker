package momomomo.dungeonwalker.wsserver.core.actor.client.command.from.engine;

import lombok.NonNull;
import momomomo.dungeonwalker.contract.engine.EngineMessageProto.EngineMessage;
import momomomo.dungeonwalker.wsserver.core.actor.client.command.ClientCommand;
import momomomo.dungeonwalker.wsserver.domain.data.common.DungeonCoordinates;

import java.util.Map;

import static java.util.stream.Collectors.toMap;

public record DungeonStateChangedCommand(
        int height,
        int width,
        @NonNull Map<String, DungeonCoordinates> dungeonState
) implements ClientCommand {

    public static DungeonStateChangedCommand of(@NonNull final EngineMessage message) {
        final var sourceDungeonState = message.getDungeonState();

        return new DungeonStateChangedCommand(
                sourceDungeonState.getHeight(),
                sourceDungeonState.getWidth(),
                sourceDungeonState
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

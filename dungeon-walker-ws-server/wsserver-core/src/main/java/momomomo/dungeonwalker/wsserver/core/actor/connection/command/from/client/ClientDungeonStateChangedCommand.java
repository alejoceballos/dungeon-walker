package momomomo.dungeonwalker.wsserver.core.actor.connection.command.from.client;

import lombok.NonNull;
import momomomo.dungeonwalker.wsserver.core.actor.client.command.from.engine.DungeonStateChangedCommand;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.ConnectionCommand;
import momomomo.dungeonwalker.wsserver.domain.data.common.DungeonCoordinates;
import momomomo.dungeonwalker.wsserver.domain.data.user.output.DungeonState;

import java.util.Map;

public record ClientDungeonStateChangedCommand(
        int height,
        int width,
        @NonNull Map<String, DungeonCoordinates> dungeonState
) implements ConnectionCommand {

    public static ClientDungeonStateChangedCommand of(@NonNull final DungeonStateChangedCommand command) {
        return new ClientDungeonStateChangedCommand(
                command.height(),
                command.width(),
                Map.copyOf(command.dungeonState()));
    }

    public DungeonState toDomain() {
        return new DungeonState(height, width, Map.copyOf(dungeonState));
    }

}

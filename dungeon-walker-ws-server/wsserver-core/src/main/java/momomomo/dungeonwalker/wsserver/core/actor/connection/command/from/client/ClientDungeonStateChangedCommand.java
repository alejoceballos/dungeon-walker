package momomomo.dungeonwalker.wsserver.core.actor.connection.command.from.client;

import lombok.NonNull;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.ConnectionCommand;
import momomomo.dungeonwalker.wsserver.domain.data.user.output.DungeonState;
import momomomo.dungeonwalker.wsserver.domain.data.common.DungeonCoordinates;

import java.util.Map;

public record ClientDungeonStateChangedCommand(
        @NonNull Map<String, DungeonCoordinates> dungeonState
) implements ConnectionCommand {

    public DungeonState toDomain() {
        return new DungeonState(Map.copyOf(dungeonState));
    }

}

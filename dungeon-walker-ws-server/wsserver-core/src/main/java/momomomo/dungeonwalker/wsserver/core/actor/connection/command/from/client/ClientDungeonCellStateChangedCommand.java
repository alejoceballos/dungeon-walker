package momomomo.dungeonwalker.wsserver.core.actor.connection.command.from.client;

import lombok.NonNull;
import momomomo.dungeonwalker.wsserver.core.actor.client.command.from.engine.DungeonCellStateChangedCommand;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.ConnectionCommand;
import momomomo.dungeonwalker.wsserver.domain.data.common.DungeonCoordinates;
import momomomo.dungeonwalker.wsserver.domain.data.user.output.CellState;

public record ClientDungeonCellStateChangedCommand(
        String id,
        @NonNull DungeonCoordinates coordinates
) implements ConnectionCommand {

    public static ClientDungeonCellStateChangedCommand of(@NonNull final DungeonCellStateChangedCommand command) {
        return new ClientDungeonCellStateChangedCommand(
                command.id(),
                command.coordinates());
    }

    public CellState toDomain() {
        return new CellState(id, coordinates);
    }

}

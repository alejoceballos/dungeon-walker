package momomomo.dungeonwalker.wsserver.core.actor.client.command.from.engine;

import lombok.NonNull;
import momomomo.dungeonwalker.contract.engine.EngineMessageProto.EngineMessage;
import momomomo.dungeonwalker.wsserver.core.actor.client.command.ClientCommand;
import momomomo.dungeonwalker.wsserver.domain.data.common.DungeonCoordinates;

public record DungeonCellStateChangedCommand(
        String id,
        @NonNull DungeonCoordinates coordinates
) implements ClientCommand {

    public static DungeonCellStateChangedCommand of(@NonNull final EngineMessage message) {
        final var sourceCellDungeonState = message.getDungeonCellState();

        return new DungeonCellStateChangedCommand(
                sourceCellDungeonState.getId(),
                new DungeonCoordinates(
                        sourceCellDungeonState.getCoordinates().getX(),
                        sourceCellDungeonState.getCoordinates().getY()));
    }

}

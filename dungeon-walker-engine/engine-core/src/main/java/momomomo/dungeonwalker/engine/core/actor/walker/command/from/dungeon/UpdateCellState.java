package momomomo.dungeonwalker.engine.core.actor.walker.command.from.dungeon;

import lombok.NonNull;
import momomomo.dungeonwalker.contract.engine.CoordinatesProto;
import momomomo.dungeonwalker.contract.engine.DungeonCellStateProto.DungeonCellState;
import momomomo.dungeonwalker.engine.core.actor.walker.command.WalkerCommand;
import momomomo.dungeonwalker.engine.domain.model.coordinates.Coordinates;

public record UpdateCellState(
        @NonNull String id,
        @NonNull Coordinates coordinates
) implements WalkerCommand {

    public DungeonCellState toProtoDungeonCellState() {
        return DungeonCellState
                .newBuilder()
                .setId(id)
                .setCoordinates(
                        CoordinatesProto.Coordinates
                                .newBuilder()
                                .setX(coordinates.x())
                                .setY(coordinates.y())
                                .build())
                .build();
    }

}

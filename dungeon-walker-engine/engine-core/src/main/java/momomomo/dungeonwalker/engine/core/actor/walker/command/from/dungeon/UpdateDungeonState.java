package momomomo.dungeonwalker.engine.core.actor.walker.command.from.dungeon;

import lombok.NonNull;
import momomomo.dungeonwalker.contract.engine.CoordinatesProto;
import momomomo.dungeonwalker.contract.engine.DungeonStateProto.DungeonState;
import momomomo.dungeonwalker.engine.core.actor.walker.command.WalkerCommand;
import momomomo.dungeonwalker.engine.domain.model.coordinates.Coordinates;

import java.util.Map;
import java.util.stream.Collectors;

public record UpdateDungeonState(
        int width,
        int height,
        @NonNull Map<String, Coordinates> coordinates
) implements WalkerCommand {

    public DungeonState toProtoDungeonState() {
        return DungeonState
                .newBuilder()
                .setHeight(height)
                .setWidth(width)
                .putAllCoordinates(coordinates
                        .entrySet()
                        .stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                e -> CoordinatesProto.Coordinates
                                        .newBuilder()
                                        .setX(e.getValue().x())
                                        .setY(e.getValue().y())
                                        .build())))
                .build();
    }

}

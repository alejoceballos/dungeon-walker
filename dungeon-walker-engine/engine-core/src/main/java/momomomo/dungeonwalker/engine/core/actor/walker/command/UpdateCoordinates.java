package momomomo.dungeonwalker.engine.core.actor.walker.command;

import akka.actor.typed.ActorRef;
import lombok.NonNull;
import momomomo.dungeonwalker.engine.core.actor.dungeon.command.DungeonCommand;
import momomomo.dungeonwalker.engine.domain.model.coordinates.Coordinates;

public record UpdateCoordinates(
        @NonNull ActorRef<DungeonCommand> dungeonRef,
        @NonNull Coordinates coordinates
) implements WalkerCommand {
}

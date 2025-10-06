package momomomo.dungeonwalker.engine.dungeon.command;

import akka.actor.typed.ActorRef;
import lombok.NonNull;
import momomomo.dungeonwalker.domain.model.coordinates.Coordinates;
import momomomo.dungeonwalker.engine.walker.command.WalkerCommand;

public record MoveWalker(
        @NonNull ActorRef<WalkerCommand> walkerRef,
        @NonNull Coordinates from,
        @NonNull Coordinates to
) implements DungeonCommand {
}

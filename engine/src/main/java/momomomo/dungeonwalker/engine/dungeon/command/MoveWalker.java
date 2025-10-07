package momomomo.dungeonwalker.engine.dungeon.command;

import akka.actor.typed.ActorRef;
import lombok.NonNull;
import momomomo.dungeonwalker.domain.model.coordinates.Coordinates;
import momomomo.dungeonwalker.engine.walker.command.WalkerCommand;

import java.util.List;

public record MoveWalker(
        @NonNull ActorRef<WalkerCommand> walkerRef,
        @NonNull Coordinates from,
        @NonNull List<Coordinates> toPossibilities
) implements DungeonCommand {
}

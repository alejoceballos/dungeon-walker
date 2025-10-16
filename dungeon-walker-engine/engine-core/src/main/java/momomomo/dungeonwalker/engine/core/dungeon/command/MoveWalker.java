package momomomo.dungeonwalker.engine.core.dungeon.command;

import akka.actor.typed.ActorRef;
import lombok.NonNull;
import momomomo.dungeonwalker.engine.domain.model.coordinates.Coordinates;
import momomomo.dungeonwalker.engine.core.walker.command.WalkerCommand;

import java.util.List;

public record MoveWalker(
        @NonNull ActorRef<WalkerCommand> walkerRef,
        @NonNull Coordinates from,
        @NonNull List<Coordinates> toPossibilities
) implements DungeonCommand {
}

package momomomo.dungeonwalker.engine.core.actor.dungeon.command;

import akka.actor.typed.ActorRef;
import lombok.NonNull;
import momomomo.dungeonwalker.engine.core.actor.walker.command.WalkerCommand;
import momomomo.dungeonwalker.engine.domain.model.coordinates.Coordinates;

import java.util.List;

public record MoveWalker(
        @NonNull ActorRef<WalkerCommand> walkerRef,
        @NonNull Coordinates from,
        @NonNull List<Coordinates> toPossibilities
) implements DungeonCommand {
}

package momomomo.dungeonwalker.engine.core.dungeon.command;

import akka.actor.typed.ActorRef;
import lombok.NonNull;
import momomomo.dungeonwalker.engine.core.walker.command.WalkerCommand;

public record PlaceWalker(@NonNull ActorRef<WalkerCommand> walkerRef) implements DungeonCommand {
}

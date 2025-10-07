package momomomo.dungeonwalker.engine.dungeon.command;

import akka.actor.typed.ActorRef;
import lombok.NonNull;
import momomomo.dungeonwalker.engine.walker.command.WalkerCommand;

public record PlaceWalker(@NonNull ActorRef<WalkerCommand> walkerRef) implements DungeonCommand {
}

package momomomo.dungeonwalker.engine.core.actor.walker.command;

import akka.actor.typed.ActorRef;
import momomomo.dungeonwalker.engine.core.actor.dungeon.command.DungeonCommand;

public record GetMoving(ActorRef<DungeonCommand> dungeonRef) implements WalkerCommand {
}

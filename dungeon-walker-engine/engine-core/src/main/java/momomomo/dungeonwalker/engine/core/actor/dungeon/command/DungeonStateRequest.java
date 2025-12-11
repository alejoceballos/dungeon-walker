package momomomo.dungeonwalker.engine.core.actor.dungeon.command;

import akka.actor.typed.ActorRef;
import lombok.NonNull;

public record DungeonStateRequest(@NonNull ActorRef<DungeonStateReply> replyTo) implements DungeonCommand {
}

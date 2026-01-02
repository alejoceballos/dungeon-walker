package momomomo.dungeonwalker.engine.core.actor.dungeon.command;

import lombok.NonNull;
import org.apache.pekko.actor.typed.ActorRef;

public record DungeonStateRequest(@NonNull ActorRef<DungeonStateReply> replyTo) implements DungeonCommand {
}

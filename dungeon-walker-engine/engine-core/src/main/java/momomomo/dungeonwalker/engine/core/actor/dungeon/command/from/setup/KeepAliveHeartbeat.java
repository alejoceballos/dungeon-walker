package momomomo.dungeonwalker.engine.core.actor.dungeon.command.from.setup;

import lombok.NonNull;
import momomomo.dungeonwalker.engine.core.actor.dungeon.command.DungeonCommand;
import momomomo.dungeonwalker.engine.core.actor.dungeon.command.to.KeepAliveReply;
import org.apache.pekko.actor.typed.ActorRef;

public record KeepAliveHeartbeat(@NonNull ActorRef<KeepAliveReply> replyTo) implements DungeonCommand {
}

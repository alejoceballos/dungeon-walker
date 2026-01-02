package momomomo.dungeonwalker.engine.core.actor.walker.command;

import lombok.NonNull;
import org.apache.pekko.actor.typed.ActorRef;

public record WalkerStateRequest(@NonNull ActorRef<WalkerStateReply> replyTo) implements WalkerCommand {
}

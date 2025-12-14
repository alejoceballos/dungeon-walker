package momomomo.dungeonwalker.engine.core.actor.walker.command;

import akka.actor.typed.ActorRef;
import lombok.NonNull;

public record WalkerStateRequest(@NonNull ActorRef<WalkerStateReply> replyTo) implements WalkerCommand {
}

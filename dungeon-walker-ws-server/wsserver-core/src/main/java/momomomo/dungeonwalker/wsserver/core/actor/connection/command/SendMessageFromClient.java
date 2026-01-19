package momomomo.dungeonwalker.wsserver.core.actor.connection.command;

import lombok.NonNull;
import momomomo.dungeonwalker.wsserver.domain.input.Input;

public record SendMessageFromClient(@NonNull Input message) implements ConnectionCommand {
}

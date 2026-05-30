package momomomo.dungeonwalker.wsserver.core.actor.connection.command.from.client;

import lombok.NonNull;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.ConnectionCommand;

public record ClientMessageCommand(@NonNull String message) implements ConnectionCommand {
}

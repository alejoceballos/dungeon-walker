package momomomo.dungeonwalker.wsserver.core.actor.connection.command.from.client;

import lombok.NonNull;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.ConnectionCommand;

public record AuthenticateFromClient(@NonNull String credentials) implements ConnectionCommand {
}

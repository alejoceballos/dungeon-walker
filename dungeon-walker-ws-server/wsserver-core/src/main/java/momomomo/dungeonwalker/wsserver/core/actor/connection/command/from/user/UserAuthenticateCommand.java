package momomomo.dungeonwalker.wsserver.core.actor.connection.command.from.user;

import lombok.NonNull;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.ConnectionCommand;

public record UserAuthenticateCommand(@NonNull String credentials) implements ConnectionCommand {
}

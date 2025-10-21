package momomomo.dungeonwalker.wsserver.core.connection.command;

import momomomo.dungeonwalker.wsserver.domain.connection.ClientConnection;

public record CloseConnection(ClientConnection connection) implements ConnectionCommand {
}

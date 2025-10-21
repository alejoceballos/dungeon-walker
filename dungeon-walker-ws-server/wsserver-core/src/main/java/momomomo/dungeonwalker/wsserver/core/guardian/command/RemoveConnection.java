package momomomo.dungeonwalker.wsserver.core.guardian.command;

import momomomo.dungeonwalker.wsserver.domain.connection.ClientConnection;

public record RemoveConnection(ClientConnection connection) implements GuardianCommand {
}

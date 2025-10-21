package momomomo.dungeonwalker.wsserver.core.guardian.command;

import momomomo.dungeonwalker.wsserver.domain.connection.ClientConnection;

public record EstablishConnection(ClientConnection connection) implements GuardianCommand {
}

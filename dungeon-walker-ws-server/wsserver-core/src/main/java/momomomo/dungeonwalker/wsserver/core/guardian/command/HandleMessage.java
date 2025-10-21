package momomomo.dungeonwalker.wsserver.core.guardian.command;

import momomomo.dungeonwalker.wsserver.domain.connection.ClientConnection;
import momomomo.dungeonwalker.wsserver.domain.input.Input;

public record HandleMessage(ClientConnection connection, Input message) implements GuardianCommand {
}

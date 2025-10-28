package momomomo.dungeonwalker.wsserver.core.sctor.connection.command;

import lombok.NonNull;
import momomomo.dungeonwalker.wsserver.domain.inbound.ClientConnection;

public record CloseConnection(@NonNull ClientConnection connection) implements ConnectionCommand {
}

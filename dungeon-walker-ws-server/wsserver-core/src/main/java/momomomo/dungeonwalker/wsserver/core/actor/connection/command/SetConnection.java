package momomomo.dungeonwalker.wsserver.core.actor.connection.command;

import lombok.NonNull;
import momomomo.dungeonwalker.wsserver.domain.inbound.ClientConnection;

public record SetConnection(@NonNull ClientConnection connection) implements ConnectionCommand {
}

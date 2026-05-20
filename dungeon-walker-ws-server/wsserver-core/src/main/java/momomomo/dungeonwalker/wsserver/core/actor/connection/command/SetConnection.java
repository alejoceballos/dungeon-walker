package momomomo.dungeonwalker.wsserver.core.actor.connection.command;

import lombok.NonNull;
import momomomo.dungeonwalker.wsserver.domain.outbound.ClientOutbound;

public record SetConnection(@NonNull ClientOutbound connection) implements ConnectionCommand {
}

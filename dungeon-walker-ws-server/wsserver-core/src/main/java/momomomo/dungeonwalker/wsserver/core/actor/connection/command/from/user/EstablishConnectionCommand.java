package momomomo.dungeonwalker.wsserver.core.actor.connection.command.from.user;

import lombok.NonNull;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.ConnectionCommand;
import momomomo.dungeonwalker.wsserver.domain.outbound.UserConnection;

public record EstablishConnectionCommand(@NonNull UserConnection userConnection) implements ConnectionCommand {
}

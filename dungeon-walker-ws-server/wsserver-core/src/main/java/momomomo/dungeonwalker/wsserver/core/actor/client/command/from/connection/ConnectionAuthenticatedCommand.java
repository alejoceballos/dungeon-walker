package momomomo.dungeonwalker.wsserver.core.actor.client.command.from.connection;

import lombok.NonNull;
import momomomo.dungeonwalker.wsserver.core.actor.client.command.ClientCommand;

public record ConnectionAuthenticatedCommand(@NonNull String connectionId) implements ClientCommand {
}

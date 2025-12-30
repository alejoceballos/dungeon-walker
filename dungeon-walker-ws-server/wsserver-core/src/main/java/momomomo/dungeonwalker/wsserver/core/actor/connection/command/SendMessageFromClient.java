package momomomo.dungeonwalker.wsserver.core.actor.connection.command;

import lombok.NonNull;
import momomomo.dungeonwalker.wsserver.core.handler.client.DataHandlerSelector;
import momomomo.dungeonwalker.wsserver.domain.inbound.ClientConnection;
import momomomo.dungeonwalker.wsserver.domain.input.Input;

public record SendMessageFromClient(
        @NonNull ClientConnection connection,
        @NonNull DataHandlerSelector dataHandlerSelector,
        @NonNull Input message
) implements ConnectionCommand {
}

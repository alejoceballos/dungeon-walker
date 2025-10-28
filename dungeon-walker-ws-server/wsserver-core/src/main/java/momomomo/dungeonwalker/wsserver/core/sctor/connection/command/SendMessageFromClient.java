package momomomo.dungeonwalker.wsserver.core.sctor.connection.command;

import lombok.NonNull;
import momomomo.dungeonwalker.wsserver.core.handler.DataHandlerSelector;
import momomomo.dungeonwalker.wsserver.domain.inbound.ClientConnection;
import momomomo.dungeonwalker.wsserver.domain.input.Input;

public record SendMessageFromClient(
        @NonNull ClientConnection connection,
        @NonNull DataHandlerSelector dataHandlerSelector,
        @NonNull Input message
) implements ConnectionCommand {
}

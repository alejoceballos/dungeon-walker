package momomomo.dungeonwalker.wsserver.core.sctor.connection.command;

import lombok.NonNull;
import momomomo.dungeonwalker.commons.DateTimeManager;
import momomomo.dungeonwalker.wsserver.core.config.HeartbeatConfig;
import momomomo.dungeonwalker.wsserver.domain.inbound.ClientConnection;

public record SetConnection(
        @NonNull ClientConnection connection,
        @NonNull DateTimeManager dateTimeManager,
        @NonNull HeartbeatConfig heartbeatConfig
) implements ConnectionCommand {
}

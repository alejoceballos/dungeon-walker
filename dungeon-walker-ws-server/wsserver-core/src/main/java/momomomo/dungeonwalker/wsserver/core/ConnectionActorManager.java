package momomomo.dungeonwalker.wsserver.core;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.commons.DateTimeManager;
import momomomo.dungeonwalker.wsserver.core.config.HeartbeatConfig;
import momomomo.dungeonwalker.wsserver.core.handler.DataHandlerSelector;
import momomomo.dungeonwalker.wsserver.core.sctor.ClusterShardingManager;
import momomomo.dungeonwalker.wsserver.core.sctor.connection.command.CloseConnection;
import momomomo.dungeonwalker.wsserver.core.sctor.connection.command.ConnectionCommand;
import momomomo.dungeonwalker.wsserver.core.sctor.connection.command.SendMessageFromClient;
import momomomo.dungeonwalker.wsserver.core.sctor.connection.command.SetConnection;
import momomomo.dungeonwalker.wsserver.domain.inbound.ClientConnection;
import momomomo.dungeonwalker.wsserver.domain.inbound.ConnectionManager;
import momomomo.dungeonwalker.wsserver.domain.input.Input;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConnectionActorManager implements ConnectionManager {

    private final ClusterShardingManager clusterShardingManager;
    private final DateTimeManager dateTimeManager;
    private final HeartbeatConfig heartbeatConfig;
    private final DataHandlerSelector dataHandlerSelector;

    @Override
    public void establish(@NonNull final ClientConnection connection) {
        log.debug("[CONNECTION - Manager] Establish connection for user \"{}\" with session \"{}\"",
                connection.getUserId(), connection.getSessionId());
        tell(connection, new SetConnection(connection, dateTimeManager, heartbeatConfig));
    }

    @Override
    public void close(@NonNull final ClientConnection connection) {
        log.debug("[CONNECTION - Manager] Close connection for user \"{}\" with session \"{}\"",
                connection.getUserId(), connection.getSessionId());

        tell(connection, new CloseConnection(connection));
    }

    @Override
    public void handleMessage(@NonNull final ClientConnection connection, @NonNull final Input message) {
        log.debug("[CONNECTION - Manager] Message received for user \"{}\" with session \"{}\": {}",
                connection.getUserId(), connection.getSessionId(), message);
        tell(connection, new SendMessageFromClient(connection, dataHandlerSelector, message));
    }

    private <C extends ConnectionCommand> void tell(
            final ClientConnection connection,
            final C command) {
        clusterShardingManager.getConnectionEntityRef(connection.getUserId()).tell(command);
    }
}

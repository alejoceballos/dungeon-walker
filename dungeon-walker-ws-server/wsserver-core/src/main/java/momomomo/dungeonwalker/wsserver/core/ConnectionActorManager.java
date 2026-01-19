package momomomo.dungeonwalker.wsserver.core;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.wsserver.core.actor.ClusterShardingManager;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.CloseConnection;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.ConnectionCommand;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.SendMessageFromClient;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.SetConnection;
import momomomo.dungeonwalker.wsserver.domain.inbound.ClientConnection;
import momomomo.dungeonwalker.wsserver.domain.inbound.ConnectionManager;
import momomomo.dungeonwalker.wsserver.domain.input.Identity;
import momomomo.dungeonwalker.wsserver.domain.input.Input;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConnectionActorManager implements ConnectionManager {

    private final ClusterShardingManager clusterShardingManager;

    @Override
    public void establish(@NonNull final ClientConnection connection) {
        log.debug("---> [CONNECTION - Manager] Establish connection for user \"{}\" with session \"{}\"",
                connection.getUserId(), connection.getSessionId());
        tell(connection, new SetConnection(connection));
        tell(connection, new SendMessageFromClient(Input.of(new Identity(connection.getUserId()))));
    }

    @Override
    public void close(@NonNull final ClientConnection connection) {
        log.debug("---> [CONNECTION - Manager] Close connection for user \"{}\" with session \"{}\"",
                connection.getUserId(), connection.getSessionId());

        tell(connection, new CloseConnection(connection));
    }

    @Override
    public void handleMessage(@NonNull final ClientConnection connection, @NonNull final Input message) {
        log.debug("---> [CONNECTION - Manager] Message received for user \"{}\" with session \"{}\": {}",
                connection.getUserId(), connection.getSessionId(), message);

        if (message.data() instanceof Identity) {
            log.warn("---> [WS Server - Handler] Identity messages are sent when establishing a connection and " +
                    "are not allowed anymore. Identity Message: \"{}\"", message.data());
            return;
        }

        tell(connection, new SendMessageFromClient(message));
    }

    private <C extends ConnectionCommand> void tell(
            final ClientConnection connection,
            final C command
    ) {
        clusterShardingManager.getConnectionEntityRef(connection.getUserId()).tell(command);
    }
}

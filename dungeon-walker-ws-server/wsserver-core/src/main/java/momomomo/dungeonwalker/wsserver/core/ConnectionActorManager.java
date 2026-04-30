package momomomo.dungeonwalker.wsserver.core;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.wsserver.core.actor.ClusterShardingManager;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.CloseConnection;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.ConnectionCommand;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.from.client.SendMessageToEngine;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.from.engine.SendWalkersPositionToClient;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.SetConnection;
import momomomo.dungeonwalker.wsserver.domain.inbound.ClientConnection;
import momomomo.dungeonwalker.wsserver.domain.inbound.ConnectionManager;
import momomomo.dungeonwalker.wsserver.domain.input.client.Identity;
import momomomo.dungeonwalker.wsserver.domain.input.client.Input;
import momomomo.dungeonwalker.wsserver.domain.input.engine.EngineMessageData;
import momomomo.dungeonwalker.wsserver.domain.input.engine.WalkersPositionsData;
import org.apache.pekko.actor.typed.ActorRef;
import org.apache.pekko.actor.typed.pubsub.Topic;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConnectionActorManager implements ConnectionManager {

    private static final String LABEL = "---> [CONNECTION - Manager]";
    private final ClusterShardingManager clusterShardingManager;
    private final ActorRef<Topic.Command<ConnectionCommand>> connectionBroadcastTopic;

    @Override
    public void establish(@NonNull final ClientConnection connection) {
        log.debug("{} Establish connection for user \"{}\" with session \"{}\"",
                LABEL, connection.getUserId(), connection.getSessionId());
        tell(connection, new SetConnection(connection));
        tell(connection, new SendMessageToEngine(Input.of(new Identity(connection.getUserId()))));
    }

    @Override
    public void close(@NonNull final ClientConnection connection) {
        log.debug("{} Close connection for user \"{}\" with session \"{}\"",
                LABEL, connection.getUserId(), connection.getSessionId());

        tell(connection, new CloseConnection(connection));
    }

    @Override
    public void handleMessage(@NonNull final ClientConnection connection, @NonNull final Input message) {
        log.debug("{} Message received for user \"{}\" with session \"{}\": {}",
                LABEL, connection.getUserId(), connection.getSessionId(), message);

        if (message.data() instanceof Identity) {
            log.warn("{} Identity messages are sent when establishing a connection and " +
                    "are not allowed anymore. Identity Message: \"{}\"", LABEL, message.data());
            return;
        }

        tell(connection, new SendMessageToEngine(message.cloneWith(connection.getUserId())));
    }

    @Override
    public void handleMessage(@NonNull final EngineMessageData message) {
        if (message.value() instanceof WalkersPositionsData(final var walkerPositions)) {
            connectionBroadcastTopic.tell(
                    Topic.publish(
                            new SendWalkersPositionToClient(walkerPositions)));
        }
    }

    private <C extends ConnectionCommand> void tell(
            final ClientConnection connection,
            final C command
    ) {
        clusterShardingManager.getConnectionEntityRef(connection.getUserId()).tell(command);
    }
}

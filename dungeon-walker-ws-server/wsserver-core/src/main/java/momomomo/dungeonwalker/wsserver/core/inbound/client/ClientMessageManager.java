package momomomo.dungeonwalker.wsserver.core.inbound.client;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.wsserver.core.actor.ClusterShardingManager;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.CloseConnection;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.SetConnection;
import momomomo.dungeonwalker.wsserver.core.inbound.client.handler.ClientInputHandlerSelector;
import momomomo.dungeonwalker.wsserver.core.inbound.client.handler.HandlerContext;
import momomomo.dungeonwalker.wsserver.domain.data.client.input.Input;
import momomomo.dungeonwalker.wsserver.domain.inbound.ClientInbound;
import momomomo.dungeonwalker.wsserver.domain.outbound.ClientOutbound;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClientMessageManager implements ClientInbound {

    private static final String LABEL = "---> [CLIENT INBOUND - Manager]";
    private final ClusterShardingManager clusterShardingManager;
    private final ClientInputHandlerSelector inputHandlerSelector;

    @Override
    public void establish(@NonNull final ClientOutbound clientOutbound) {
        log.debug("{} Establish connection with session \"{}\"", LABEL, clientOutbound.getId());
        clusterShardingManager
                .getConnectionEntityRef(clientOutbound.getId())
                .tell(new SetConnection(clientOutbound));
    }

    @Override
    public void close(@NonNull final String clientId) {
        log.debug("{} Close connection for session \"{}\"", LABEL, clientId);
        clusterShardingManager
                .getConnectionEntityRef(clientId)
                .tell(new CloseConnection());
    }

    @Override
    public void handleMessage(@NonNull final String clientOutboundId, @NonNull final Input input) {
        log.debug("{} Message received for session \"{}\": {}", LABEL, clientOutboundId, input);
        inputHandlerSelector
                .select(input.data())
                .handle(new HandlerContext(clientOutboundId, input.data()));
    }

}

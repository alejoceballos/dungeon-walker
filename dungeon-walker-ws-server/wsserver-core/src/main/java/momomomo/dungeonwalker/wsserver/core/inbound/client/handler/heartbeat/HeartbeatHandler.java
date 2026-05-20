package momomomo.dungeonwalker.wsserver.core.inbound.client.handler.heartbeat;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.wsserver.core.actor.ClusterShardingManager;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.from.client.HeartbeatFromClient;
import momomomo.dungeonwalker.wsserver.core.inbound.client.handler.ClientInputMapper;
import momomomo.dungeonwalker.wsserver.core.inbound.client.handler.SelectableClientInputHandler;
import momomomo.dungeonwalker.wsserver.domain.data.client.input.ClientHeartbeat;
import momomomo.dungeonwalker.wsserver.domain.data.client.input.InputData;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HeartbeatHandler extends SelectableClientInputHandler<ClientHeartbeat> {

    public HeartbeatHandler(
            final ClientInputMapper<ClientHeartbeat, HeartbeatFromClient> mapper,
            final ClusterShardingManager clusterShardingManager
    ) {
        super(mapper, clusterShardingManager);
    }

    @Override
    public boolean canHandle(@NonNull final InputData message) {
        return message instanceof ClientHeartbeat;
    }

}

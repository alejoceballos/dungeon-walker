package momomomo.dungeonwalker.wsserver.core.inbound.client.handler.heartbeat;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.from.client.HeartbeatFromClient;
import momomomo.dungeonwalker.wsserver.core.inbound.client.handler.ClientInputMapper;
import momomomo.dungeonwalker.wsserver.domain.data.client.input.ClientHeartbeat;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HeartbeatMapper implements ClientInputMapper<ClientHeartbeat, HeartbeatFromClient> {

    @Override
    public @NonNull HeartbeatFromClient map(@NonNull final ClientHeartbeat inputData) {
        return new HeartbeatFromClient(inputData.timestamp());
    }

}

package momomomo.dungeonwalker.engine.core.inbound.client.mapper.heartbeat;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import momomomo.dungeonwalker.contract.client.ClientRequestProto.ClientRequest;
import momomomo.dungeonwalker.engine.core.actor.walker.command.from.client.UserHeartbeat;
import momomomo.dungeonwalker.engine.core.inbound.client.mapper.ClientRequestMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserHeartbeatMapper implements ClientRequestMapper<UserHeartbeat> {

    @Override
    public @NonNull UserHeartbeat map(@NonNull final ClientRequest request) {
        return new UserHeartbeat();
    }

    @Override
    public boolean canMap(@NonNull final ClientRequest message) {
        return message.hasHeartbeat();
    }

}

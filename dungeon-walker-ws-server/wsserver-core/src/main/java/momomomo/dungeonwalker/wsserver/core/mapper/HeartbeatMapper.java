package momomomo.dungeonwalker.wsserver.core.mapper;

import jakarta.annotation.Nonnull;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.contract.client.ClientRequestProto.ClientRequest;
import momomomo.dungeonwalker.wsserver.domain.input.ClientHeartbeat;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HeartbeatMapper implements InputDataMapper<ClientHeartbeat, ClientRequest> {

    @Override
    public @Nonnull ClientRequest map(@NonNull final ClientHeartbeat inputData) {
        throw new UnsupportedOperationException("Heartbeat messages are not supposed to be mapped.");
    }

}

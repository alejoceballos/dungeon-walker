package momomomo.dungeonwalker.engine.core.inbound.client;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.contract.client.ClientRequestProto.ClientRequest;
import momomomo.dungeonwalker.engine.core.actor.ClusterShardingManager;
import momomomo.dungeonwalker.engine.core.inbound.client.mapper.ClientRequestMapperSelector;
import momomomo.dungeonwalker.engine.domain.inbound.ClientInbound;
import org.springframework.stereotype.Component;

import static java.util.Objects.nonNull;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClientMessageManager implements ClientInbound<ClientRequest> {

    private static final String LABEL = "---> [CLIENT INBOUND - Manager]";

    private final ClusterShardingManager clusterShardingManager;
    private final ClientRequestMapperSelector mapperSelector;

    @Override
    public void handleMessage(@NonNull final ClientRequest request) {
        log.debug("{} Client request received: {}", LABEL, request);

        final var mapper = mapperSelector.select(request);

        if (nonNull(mapper)) {
            clusterShardingManager.tellWalker(request.getClientId(), mapper.map(request));

        } else {
            log.error("{} No mapper found for request: {}", LABEL, request);
        }
    }

}

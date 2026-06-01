package momomomo.dungeonwalker.wsserver.core.inbound.engine;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.contract.engine.EngineMessageProto.EngineMessage;
import momomomo.dungeonwalker.wsserver.core.actor.ClusterShardingManager;
import momomomo.dungeonwalker.wsserver.core.actor.client.command.ClientCommand;
import momomomo.dungeonwalker.wsserver.core.inbound.engine.mapper.EngineMessageMapperSelector;
import momomomo.dungeonwalker.wsserver.domain.inbound.EngineInbound;
import org.springframework.stereotype.Component;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Component
@RequiredArgsConstructor
public class EngineMessageManager implements EngineInbound<EngineMessage> {

    private static final String LABEL = "---> [ENGINE INBOUND - Manager]";

    private final ClusterShardingManager clusterShardingManager;
    private final EngineMessageMapperSelector mapperSelector;

    @Override
    public void handleMessage(@NonNull final EngineMessage message) {
        log.debug("{} Engine message received: {}", LABEL, message);

        final var mapper = mapperSelector.select(message);

        if (nonNull(mapper)) {
            if (isBlank(message.getTarget())) {
                // TODO: Future development. Broadcast to all clients
                log.error("{} Engine message has no client target: {}", LABEL, message);

            } else {
                tellClientTo(message.getTarget(), mapper.map(message));
            }

        } else {
            log.error("{} No mapper found for message: {}", LABEL, message);
        }
    }

    private void tellClientTo(final String clientId, final ClientCommand command) {
        clusterShardingManager.getClientRef(clientId).tell(command);
    }

}

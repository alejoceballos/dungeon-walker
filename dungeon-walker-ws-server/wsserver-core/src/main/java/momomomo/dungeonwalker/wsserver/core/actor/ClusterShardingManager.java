package momomomo.dungeonwalker.wsserver.core.actor;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.contract.engine.EngineMessageProto.EngineMessage;
import momomomo.dungeonwalker.wsserver.core.actor.connection.ConnectionActor;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.ConnectionCommand;
import momomomo.dungeonwalker.wsserver.domain.handler.MessageHandlerSelector;
import momomomo.dungeonwalker.wsserver.domain.inbound.ConsumerFactory;
import org.apache.pekko.cluster.sharding.typed.javadsl.ClusterSharding;
import org.apache.pekko.cluster.sharding.typed.javadsl.Entity;
import org.apache.pekko.cluster.sharding.typed.javadsl.EntityRef;
import org.apache.pekko.cluster.sharding.typed.javadsl.EntityTypeKey;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClusterShardingManager {

    private final ClusterSharding clusterSharding;
    private final EntityTypeKey<ConnectionCommand> connectionEntityTypeKey;
    private final ConsumerFactory<EngineMessage> consumerFactory;
    private final MessageHandlerSelector<EngineMessage, Void> messageHandlerSelector;

    @PostConstruct
    public void init() {
        log.debug("---> [CLUSTER - Manager] Bean initialized");
        clusterSharding.init(
                Entity.of(
                        connectionEntityTypeKey,
                        _ -> ConnectionActor.create(consumerFactory, messageHandlerSelector)));
    }

    public EntityRef<ConnectionCommand> getConnectionEntityRef(final String id) {
        log.debug("---> [CLUSTER - Manager] get connection entity ref \"{}\"", id);
        return clusterSharding.entityRefFor(connectionEntityTypeKey, id);
    }

}

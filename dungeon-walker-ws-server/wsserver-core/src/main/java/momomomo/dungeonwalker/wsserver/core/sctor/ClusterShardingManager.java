package momomomo.dungeonwalker.wsserver.core.sctor;

import akka.cluster.sharding.typed.javadsl.ClusterSharding;
import akka.cluster.sharding.typed.javadsl.Entity;
import akka.cluster.sharding.typed.javadsl.EntityRef;
import akka.cluster.sharding.typed.javadsl.EntityTypeKey;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.wsserver.core.sctor.connection.ConnectionActor;
import momomomo.dungeonwalker.wsserver.core.sctor.connection.command.ConnectionCommand;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClusterShardingManager {

    private final ClusterSharding clusterSharding;
    private final EntityTypeKey<ConnectionCommand> connectionEntityTypeKey;

    @PostConstruct
    public void init() {
        log.debug("[CLUSTER - Manager] Bean initialized");
        clusterSharding.init(
                Entity.of(
                        connectionEntityTypeKey,
                        context -> ConnectionActor.create()));
    }

    public EntityRef<ConnectionCommand> getConnectionEntityRef(final String id) {
        log.debug("[CLUSTER - Manager] get connection entity ref \"{}\"", id);
        return clusterSharding.entityRefFor(connectionEntityTypeKey, id);
    }

}

package momomomo.dungeonwalker.wsserver.core.actor;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.contract.client.ClientRequestProto;
import momomomo.dungeonwalker.wsserver.core.actor.client.ClientActor;
import momomomo.dungeonwalker.wsserver.core.actor.client.command.ClientCommand;
import momomomo.dungeonwalker.wsserver.core.actor.connection.ConnectionActor;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.ConnectionCommand;
import momomomo.dungeonwalker.wsserver.domain.auth.Authorizer;
import momomomo.dungeonwalker.wsserver.domain.data.engine.output.EngineOutbound;
import org.apache.pekko.cluster.sharding.typed.javadsl.ClusterSharding;
import org.apache.pekko.cluster.sharding.typed.javadsl.Entity;
import org.apache.pekko.cluster.sharding.typed.javadsl.EntityRef;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClusterShardingManager {

    private static final String LABEL = "---> [CLUSTER - Manager]";

    private final ClusterSharding clusterSharding;
    private final Authorizer authorizer;
    private final EngineOutbound<ClientRequestProto.ClientRequest> engineOutbound;

    @PostConstruct
    public void init() {
        log.debug("{} Bean initializing", LABEL);

        clusterSharding.init(
                Entity.of(
                        ConnectionActor.ENTITY_TYPE_KEY,
                        _ -> ConnectionActor.create(
                                authorizer,
                                clusterSharding)));

        clusterSharding.init(
                Entity.of(
                        ClientActor.ENTITY_TYPE_KEY,
                        _ -> ClientActor.create(
                                clusterSharding,
                                engineOutbound)));
    }

    public EntityRef<ClientCommand> getClientRef(final String id) {
        log.debug("{} get client entity ref \"{}\"", LABEL, id);
        return clusterSharding.entityRefFor(ClientActor.ENTITY_TYPE_KEY, id);
    }

    public EntityRef<ConnectionCommand> getConnectionRef(final String id) {
        log.debug("{} get inbound entity ref \"{}\"", LABEL, id);
        return clusterSharding.entityRefFor(ConnectionActor.ENTITY_TYPE_KEY, id);
    }

}

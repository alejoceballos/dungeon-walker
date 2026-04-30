package momomomo.dungeonwalker.wsserver.core.actor;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.commons.DateTimeManager;
import momomomo.dungeonwalker.contract.client.ClientRequestProto;
import momomomo.dungeonwalker.wsserver.core.actor.connection.ConnectionActor;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.ConnectionCommand;
import momomomo.dungeonwalker.wsserver.core.config.properties.heartbeat.HeartbeatProps;
import momomomo.dungeonwalker.wsserver.core.handler.client.DataHandlerSelector;
import momomomo.dungeonwalker.wsserver.domain.outbound.Sender;
import org.apache.pekko.actor.typed.ActorRef;
import org.apache.pekko.actor.typed.pubsub.Topic;
import org.apache.pekko.cluster.sharding.typed.javadsl.ClusterSharding;
import org.apache.pekko.cluster.sharding.typed.javadsl.Entity;
import org.apache.pekko.cluster.sharding.typed.javadsl.EntityRef;
import org.springframework.stereotype.Component;

import static momomomo.dungeonwalker.wsserver.core.actor.connection.ConnectionActor.ENTITY_TYPE_KEY;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClusterShardingManager {

    private static final String LABEL = "---> [CLUSTER - Manager]";

    private final ClusterSharding clusterSharding;
    private final ActorRef<Topic.Command<ConnectionCommand>> connectionBroadcastTopic;
    private final DataHandlerSelector dataHandlerSelector;
    private final DateTimeManager dateTimeManager;
    private final HeartbeatProps heartbeatProps;
    private final Sender<ClientRequestProto.ClientRequest> sender;

    @PostConstruct
    public void init() {
        log.debug("{} Bean initializing", LABEL);

        clusterSharding.init(
                Entity.of(
                        ENTITY_TYPE_KEY,
                        _ -> ConnectionActor.create(
                                connectionBroadcastTopic,
                                dataHandlerSelector,
                                dateTimeManager,
                                heartbeatProps,
                                sender)));
    }

    public EntityRef<ConnectionCommand> getConnectionEntityRef(final String id) {
        log.debug("{} get connection entity ref \"{}\"", LABEL, id);
        return clusterSharding.entityRefFor(ENTITY_TYPE_KEY, id);
    }

}

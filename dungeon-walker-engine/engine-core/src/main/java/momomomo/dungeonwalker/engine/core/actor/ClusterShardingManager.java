package momomomo.dungeonwalker.engine.core.actor;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.contract.engine.EngineMessageProto;
import momomomo.dungeonwalker.engine.core.actor.dungeon.DungeonActor;
import momomomo.dungeonwalker.engine.core.actor.dungeon.command.DungeonCommand;
import momomomo.dungeonwalker.engine.core.actor.walker.WalkerActor;
import momomomo.dungeonwalker.engine.core.actor.walker.command.WalkerCommand;
import momomomo.dungeonwalker.engine.core.setup.DungeonIdentity;
import momomomo.dungeonwalker.engine.domain.outbound.ClientOutbound;
import org.apache.pekko.actor.typed.ActorRef;
import org.apache.pekko.actor.typed.pubsub.Topic;
import org.apache.pekko.cluster.sharding.typed.javadsl.ClusterSharding;
import org.apache.pekko.cluster.sharding.typed.javadsl.Entity;
import org.apache.pekko.cluster.sharding.typed.javadsl.EntityRef;
import org.apache.pekko.persistence.typed.PersistenceId;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClusterShardingManager {

    private static final String LABEL = "---> [CLUSTER - Manager]";

    private final ClusterSharding clusterSharding;
    private final ActorRef<Topic.Command<WalkerCommand>> walkerBroadcastTopic;

    private final DungeonIdentity dungeonIdentity;
    private final ClientOutbound<EngineMessageProto.EngineMessage> clientOutbound;

    @PostConstruct
    public void init() {
        log.debug("{} Bean initializing", LABEL);

        log.debug("{} Initializing dungeon actor", LABEL);
        clusterSharding.init(
                Entity.of(
                        DungeonActor.ENTITY_TYPE_KEY,
                        context -> DungeonActor.create(
                                walkerBroadcastTopic,
                                PersistenceId.of(
                                        context.getEntityTypeKey().name(),
                                        context.getEntityId()))));

        log.debug("{} Initializing walker actor", LABEL);
        clusterSharding.init(
                Entity.of(
                        WalkerActor.ENTITY_TYPE_KEY,
                        context -> WalkerActor.create(
                                walkerBroadcastTopic,
                                PersistenceId.of(
                                        context.getEntityTypeKey().name(),
                                        context.getEntityId()),
                                dungeonIdentity,
                                clientOutbound)));
    }

    public EntityRef<DungeonCommand> dungeonRef(final String entityId) {
        return clusterSharding.entityRefFor(DungeonActor.ENTITY_TYPE_KEY, entityId);
    }

    public void tellDungeon(final String entityId, final DungeonCommand command) {
        log.debug("{} tell dungeon \"{}\" {}", LABEL, entityId, command.getClass().getSimpleName());
        dungeonRef(entityId).tell(command);
    }

    public void tellWalker(final String entityId, final WalkerCommand command) {
        log.debug("{} tell walker \"{}\" {}", LABEL, entityId, command.getClass().getSimpleName());
        clusterSharding.entityRefFor(WalkerActor.ENTITY_TYPE_KEY, entityId).tell(command);
    }

}

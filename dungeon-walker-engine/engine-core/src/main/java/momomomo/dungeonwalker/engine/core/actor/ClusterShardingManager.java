package momomomo.dungeonwalker.engine.core.actor;

import akka.cluster.sharding.typed.javadsl.ClusterSharding;
import akka.cluster.sharding.typed.javadsl.Entity;
import akka.cluster.sharding.typed.javadsl.EntityRef;
import akka.persistence.typed.PersistenceId;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.engine.core.actor.dungeon.DungeonActor;
import momomomo.dungeonwalker.engine.core.actor.dungeon.command.DungeonCommand;
import momomomo.dungeonwalker.engine.core.actor.walker.WalkerActor;
import momomomo.dungeonwalker.engine.core.actor.walker.command.WalkerCommand;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClusterShardingManager {

    private final ClusterSharding clusterSharding;

    @PostConstruct
    public void init() {
        log.debug("---> [CLUSTER - Manager] Bean initializing");

        log.debug("---> [CLUSTER - Manager] Initializing dungeon actor");
        clusterSharding.init(
                Entity.of(
                        DungeonActor.ENTITY_TYPE_KEY,
                        context -> DungeonActor.create(
                                PersistenceId.of(
                                        context.getEntityTypeKey().name(),
                                        context.getEntityId()))));

        log.debug("---> [CLUSTER - Manager] Initializing walker actor");
        clusterSharding.init(
                Entity.of(
                        WalkerActor.ENTITY_TYPE_KEY,
                        context -> WalkerActor.create(
                                PersistenceId.of(
                                        context.getEntityTypeKey().name(),
                                        context.getEntityId()))));
    }

    public EntityRef<DungeonCommand> getDungeonEntityRef(final String entityId) {
        log.debug("---> [CLUSTER - Manager] get dungeon entity ref \"{}\"", entityId);
        return clusterSharding.entityRefFor(DungeonActor.ENTITY_TYPE_KEY, entityId);
    }

    public EntityRef<WalkerCommand> getWalkerEntityRef(final String entityId) {
        log.debug("---> [CLUSTER - Manager] get walker entity ref \"{}\"", entityId);
        return clusterSharding.entityRefFor(WalkerActor.ENTITY_TYPE_KEY, entityId);
    }

}

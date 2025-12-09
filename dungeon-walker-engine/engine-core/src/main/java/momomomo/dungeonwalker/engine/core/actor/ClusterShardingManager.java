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
import momomomo.dungeonwalker.engine.core.actor.walker.AutomatedWalkerActor;
import momomomo.dungeonwalker.engine.core.actor.walker.UserWalkerActor;
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

        log.debug("---> [CLUSTER - Manager] Initializing automated walker actor");
        clusterSharding.init(
                Entity.of(
                        AutomatedWalkerActor.ENTITY_TYPE_KEY,
                        context -> AutomatedWalkerActor.create(
                                PersistenceId.of(
                                        context.getEntityTypeKey().name(),
                                        context.getEntityId()))));

        log.debug("---> [CLUSTER - Manager] Initializing user walker actor");
        clusterSharding.init(
                Entity.of(
                        UserWalkerActor.ENTITY_TYPE_KEY,
                        context -> UserWalkerActor.create(
                                PersistenceId.of(
                                        context.getEntityTypeKey().name(),
                                        context.getEntityId()))));
    }

    public EntityRef<DungeonCommand> getDungeonEntityRef(final String entityId) {
        log.debug("---> [CLUSTER - Manager] get dungeon entity ref \"{}\"", entityId);
        return clusterSharding.entityRefFor(DungeonActor.ENTITY_TYPE_KEY, entityId);
    }

    public EntityRef<WalkerCommand> getAutomatedWalkerEntityRef(final String entityId) {
        log.debug("---> [CLUSTER - Manager] get automatic walker entity ref \"{}\"", entityId);
        return clusterSharding.entityRefFor(AutomatedWalkerActor.ENTITY_TYPE_KEY, entityId);
    }

    public EntityRef<WalkerCommand> getUserWalkerEntityRef(final String entityId) {
        log.debug("---> [CLUSTER - Manager] get user walker entity ref \"{}\"", entityId);
        return clusterSharding.entityRefFor(UserWalkerActor.ENTITY_TYPE_KEY, entityId);
    }

}

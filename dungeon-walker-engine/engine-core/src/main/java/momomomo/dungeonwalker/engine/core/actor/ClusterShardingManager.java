package momomomo.dungeonwalker.engine.core.actor;

import akka.actor.typed.ActorSystem;
import akka.cluster.sharding.typed.javadsl.ClusterSharding;
import akka.cluster.sharding.typed.javadsl.Entity;
import akka.cluster.sharding.typed.javadsl.EntityRef;
import akka.persistence.typed.PersistenceId;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
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

    private static final String LABEL = "---> [CLUSTER - Manager]";

    private final ClusterSharding clusterSharding;

    @Getter
    private final ActorSystem<Void> actorSystem;

    @PostConstruct
    public void init() {
        log.debug("{} Bean initializing", LABEL);

        log.debug("{} Initializing dungeon actor", LABEL);
        clusterSharding.init(
                Entity.of(
                        DungeonActor.ENTITY_TYPE_KEY,
                        context -> DungeonActor.create(
                                PersistenceId.of(
                                        context.getEntityTypeKey().name(),
                                        context.getEntityId()))));

        log.debug("{} Initializing automated walker actor", LABEL);
        clusterSharding.init(
                Entity.of(
                        AutomatedWalkerActor.ENTITY_TYPE_KEY,
                        context -> AutomatedWalkerActor.create(
                                PersistenceId.of(
                                        context.getEntityTypeKey().name(),
                                        context.getEntityId()))));

        log.debug("{} Initializing user walker actor", LABEL);
        clusterSharding.init(
                Entity.of(
                        UserWalkerActor.ENTITY_TYPE_KEY,
                        context -> UserWalkerActor.create(
                                PersistenceId.of(
                                        context.getEntityTypeKey().name(),
                                        context.getEntityId()))));
    }

    public EntityRef<DungeonCommand> getDungeonEntityRef(final String entityId) {
        log.debug("{} get dungeon entity ref \"{}\"", LABEL, entityId);
        return clusterSharding.entityRefFor(DungeonActor.ENTITY_TYPE_KEY, entityId);
    }

    public EntityRef<WalkerCommand> getAutomatedWalkerEntityRef(final String entityId) {
        log.debug("{} get automatic walker entity ref \"{}\"", LABEL, entityId);
        return clusterSharding.entityRefFor(AutomatedWalkerActor.ENTITY_TYPE_KEY, entityId);
    }

    public EntityRef<WalkerCommand> getUserWalkerEntityRef(final String entityId) {
        log.debug("{} get user walker entity ref \"{}\"", LABEL, entityId);
        return clusterSharding.entityRefFor(UserWalkerActor.ENTITY_TYPE_KEY, entityId);
    }

}

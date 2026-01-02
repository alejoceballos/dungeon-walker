package momomomo.dungeonwalker.wsserver.core.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.ConnectionCommand;
import momomomo.dungeonwalker.wsserver.core.actor.guardian.GuardianActor;
import org.apache.pekko.actor.typed.ActorSystem;
import org.apache.pekko.cluster.sharding.typed.javadsl.ClusterSharding;
import org.apache.pekko.cluster.sharding.typed.javadsl.EntityTypeKey;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Slf4j
@Configuration
public class PekkoConfig {

    @Bean
    public Config pekkoConfiguration() {
        log.info("---> [PEKKO - Config] 'application.conf' loaded and 'Config' bean created");
        return ConfigFactory.load("application.conf");
    }

    @Bean
    @DependsOn("pekkoConfiguration")
    public ActorSystem<Void> actorSystem() {
        log.info("---> [PEKKO - Config] 'actorSystem' bean created");
        return ActorSystem.create(GuardianActor.create(), "WsServerClusterSystem");
    }

    @Bean
    @DependsOn("actorSystem")
    public ClusterSharding clusterSharding(@NonNull final ActorSystem<Void> guardian) {
        log.info("---> [PEKKO - Config] 'clusterSharding' bean created");
        return ClusterSharding.get(guardian);
    }

    @Bean
    @DependsOn("clusterSharding")
    public EntityTypeKey<ConnectionCommand> connectionEntityTypeKey() {
        log.info("---> [PEKKO - Config] 'connectionEntityTypeKey' bean created");
        return EntityTypeKey.create(ConnectionCommand.class, "connection-actor-type-key");
    }

}

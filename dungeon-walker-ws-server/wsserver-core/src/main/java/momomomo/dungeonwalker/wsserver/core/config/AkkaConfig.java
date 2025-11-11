package momomomo.dungeonwalker.wsserver.core.config;

import akka.actor.typed.ActorSystem;
import akka.cluster.sharding.typed.javadsl.ClusterSharding;
import akka.cluster.sharding.typed.javadsl.EntityTypeKey;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.wsserver.core.sctor.connection.command.ConnectionCommand;
import momomomo.dungeonwalker.wsserver.core.sctor.guardian.GuardianActor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Slf4j
@Configuration
public class AkkaConfig {

    @Bean
    public Config akkaConfiguration() {
        log.info("---> [AKKA - Config] 'application.conf' loaded and 'akkaConfiguration' bean created");
        return ConfigFactory.load("application.conf");
    }

    @Bean
    @DependsOn("akkaConfiguration")
    public ActorSystem<Void> actorSystem() {
        log.info("---> [AKKA - Config] 'actorSystem' bean created");
        return ActorSystem.create(GuardianActor.create(), "WsServerActorSystem");
    }

    @Bean
    @DependsOn("actorSystem")
    public ClusterSharding clusterSharding(@NonNull final ActorSystem<Void> guardian) {
        log.info("---> [AKKA - Config] 'clusterSharding' bean created");
        return ClusterSharding.get(guardian);
    }

    @Bean
    @DependsOn("clusterSharding")
    public EntityTypeKey<ConnectionCommand> connectionEntityTypeKey() {
        log.info("---> [AKKA - Config] 'connectionEntityTypeKey' bean created");
        return EntityTypeKey.create(ConnectionCommand.class, "connection-actor-type-key");
    }

}

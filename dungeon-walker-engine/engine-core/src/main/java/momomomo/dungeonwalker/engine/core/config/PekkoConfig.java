package momomomo.dungeonwalker.engine.core.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.engine.core.actor.guardian.GuardianActor;
import org.apache.pekko.actor.typed.ActorSystem;
import org.apache.pekko.cluster.sharding.typed.javadsl.ClusterSharding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Slf4j
@Configuration
public class PekkoConfig {

    @Bean
    public Config pekkoConfiguration() {
        log.info("---> [PEKKO - Config] 'application.conf' load and 'config' bean created");
        return ConfigFactory.load("application.conf");
    }

    @Bean
    @DependsOn("pekkoConfiguration")
    public ActorSystem<Void> actorSystem() {
        log.info("---> [PEKKO - Config] 'actorSystem' bean created");
        return ActorSystem.create(GuardianActor.create(), "EngineClusterSystem");
    }

    @Bean
    @DependsOn("actorSystem")
    public ClusterSharding clusterSharding(@NonNull final ActorSystem<Void> actorSystem) {
        log.info("---> [PEKKO - Config] 'clusterSharding' bean created");
        return ClusterSharding.get(actorSystem);
    }

}

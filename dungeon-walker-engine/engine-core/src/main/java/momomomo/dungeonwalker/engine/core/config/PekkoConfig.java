package momomomo.dungeonwalker.engine.core.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.engine.core.actor.guardian.GuardianActor;
import momomomo.dungeonwalker.engine.core.config.properties.pekko.PekkoProps;
import momomomo.dungeonwalker.spring.commons.YamlPropertySourceFactory;
import org.apache.pekko.actor.typed.ActorSystem;
import org.apache.pekko.cluster.sharding.typed.javadsl.ClusterSharding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;

import java.util.Properties;

@Slf4j
@Configuration
@PropertySource(value = "classpath:application-engine.yml", factory = YamlPropertySourceFactory.class)
@RequiredArgsConstructor
public class PekkoConfig {

    private final PekkoProps pekko;

    @Bean
    public Config pekkoConfiguration() {
        log.info("---> [PEKKO - Config] bean created");

        final var properties = new Properties();
        properties.put("pekko.persistence.r2dbc.dialect", pekko.getPersistence().getR2dbc().getDialect());
        properties.put("pekko.persistence.r2dbc.connection-factory.driver", pekko.getPersistence().getR2dbc().getConnectionFactory().getDriver());
        properties.put("pekko.persistence.r2dbc.connection-factory.host", pekko.getPersistence().getR2dbc().getConnectionFactory().getHost());
        properties.put("pekko.persistence.r2dbc.connection-factory.port", pekko.getPersistence().getR2dbc().getConnectionFactory().getPort());
        properties.put("pekko.persistence.r2dbc.connection-factory.database", pekko.getPersistence().getR2dbc().getConnectionFactory().getDatabase());
        properties.put("pekko.persistence.r2dbc.connection-factory.user", pekko.getPersistence().getR2dbc().getConnectionFactory().getUser());
        properties.put("pekko.persistence.r2dbc.connection-factory.password", pekko.getPersistence().getR2dbc().getConnectionFactory().getPassword());
        properties.put("pekko.remote.artery.canonical.hostname", pekko.getRemote().getArtery().getCanonical().getHostname());
        properties.put("pekko.remote.artery.canonical.port", pekko.getRemote().getArtery().getCanonical().getPort());

        for (int i = 0; i < pekko.getCluster().getSeedNodes().length; i++) {
            properties.put("pekko.cluster.seed-nodes." + i, pekko.getCluster().getSeedNodes()[i]);
        }

        return ConfigFactory
                .parseProperties(properties)
                .withFallback(ConfigFactory.load("application.conf"));
    }

    @Bean
    @DependsOn("pekkoConfiguration")
    public ActorSystem<Void> actorSystem(final Config pekkoConfiguration) {
        log.info("---> [PEKKO - Config] 'actorSystem' bean created");
        return ActorSystem.create(GuardianActor.create(), "EngineClusterSystem", pekkoConfiguration);
    }

    @Bean
    @DependsOn("actorSystem")
    public ClusterSharding clusterSharding(@NonNull final ActorSystem<Void> actorSystem) {
        log.info("---> [PEKKO - Config] 'clusterSharding' bean created");
        return ClusterSharding.get(actorSystem);
    }

}

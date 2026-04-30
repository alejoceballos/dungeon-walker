package momomomo.dungeonwalker.wsserver.core.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.wsserver.core.actor.TopicWrapper;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.ConnectionCommand;
import momomomo.dungeonwalker.wsserver.core.actor.guardian.GuardianActor;
import momomomo.dungeonwalker.wsserver.core.config.properties.pekko.PekkoProps;
import org.apache.pekko.actor.typed.ActorRef;
import org.apache.pekko.actor.typed.ActorSystem;
import org.apache.pekko.actor.typed.pubsub.Topic;
import org.apache.pekko.cluster.sharding.typed.javadsl.ClusterSharding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.util.Properties;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class PekkoConfig {

    private static final String LABEL = "---> [PEKKO - Config]";

    private final PekkoProps pekko;

    private final TopicWrapper topicWrapper = new TopicWrapper();

    @Bean
    public Config pekkoConfiguration() {
        final var properties = new Properties();

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
        return ActorSystem.create(GuardianActor.create(topicWrapper), "WsServerClusterSystem", pekkoConfiguration);
    }

    @Bean
    @DependsOn("actorSystem")
    public ClusterSharding clusterSharding(@NonNull final ActorSystem<Void> actorSystem) {
        return ClusterSharding.get(actorSystem);
    }

    @Bean
    @DependsOn("actorSystem")
    public ActorRef<Topic.Command<ConnectionCommand>> connectionBroadcastTopic() {
        return topicWrapper.getConnectionBroadcastTopic();
    }

}

package momomomo.dungeonwalker.wsserver.startup;

import io.cucumber.spring.CucumberContextConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

@SuppressWarnings("java:S2187")
@Slf4j
@DirtiesContext
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@CucumberContextConfiguration
public class DungeonWalkerWsServerIntegrationTests {

    @LocalServerPort
    protected Integer port;

    public static final KafkaContainer kafka =
            new KafkaContainer(DockerImageName.parse("apache/kafka:4.1.1"))
                    .withExposedPorts(9092)
                    .withEnv("KAFKA_AUTO_CREATE_TOPICS_ENABLE", "false")
                    .withEnv("KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR", "1");

    static {
        kafka.setPortBindings(List.of("9092:9092"));
        kafka.start();

        final var bootstrapServers = kafka.getBootstrapServers();
        System.setProperty("KAFKA_BOOTSTRAP_SERVERS", bootstrapServers);

        try {
            createTopics(bootstrapServers);

        } catch (final ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates Kafka topics that the tests need.
     * Define your topic names here based on your application's requirements.
     */
    private static void createTopics(final String bootstrapServers) throws ExecutionException, InterruptedException {
        final var adminProps = new Properties();
        adminProps.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        try (final var adminClient = AdminClient.create(adminProps)) {
            final var topics = List.of(
                    new NewTopic("game-engine-consumer-topic", 1, (short) 1),
                    new NewTopic("game-engine-producer-topic", 1, (short) 1)
            );

            final var createResult = adminClient.createTopics(topics);

            // Wait for topic creation to complete
            createResult.all().get();
        }
    }

}
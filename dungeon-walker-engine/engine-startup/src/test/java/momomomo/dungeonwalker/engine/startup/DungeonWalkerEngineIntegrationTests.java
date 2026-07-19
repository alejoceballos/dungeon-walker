package momomomo.dungeonwalker.engine.startup;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.spring.CucumberContextConfiguration;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.engine.startup.steps.DungeonWalkerEngineStepsDef;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.AfterAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@SuppressWarnings({"resource", "java:S2187"})
@Slf4j
@DirtiesContext
@Testcontainers
@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DungeonWalkerEngineIntegrationTests {

    @Autowired
    protected ObjectMapper jsonMapper;

    public static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>(
            DockerImageName.parse("postgres:18-alpine"))
            .withDatabaseName("postgres")
            .withUsername("postgres")
            .withPassword("postgres")
            .withExposedPorts(5432)
            .withCopyFileToContainer(
                    MountableFile.forClasspathResource("init-db.sql"),
                    "/docker-entrypoint-initdb.d/");

    static {
        POSTGRES.setPortBindings(List.of("5432:5432"));
        POSTGRES.start();
    }

    public static final KafkaContainer KAFKA = new KafkaContainer(
            DockerImageName.parse("apache/kafka:4.1.1"))
            .withExposedPorts(9092)
            .withEnv("KAFKA_AUTO_CREATE_TOPICS_ENABLE", "false")
            .withEnv("KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR", "1");

    static {
        KAFKA.setPortBindings(List.of("9092:9092"));
        KAFKA.start();

        final var bootstrapServers = KAFKA.getBootstrapServers();
        System.setProperty("KAFKA_BOOTSTRAP_SERVERS", bootstrapServers);

        try {
            createTopics(bootstrapServers);

        } catch (final ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterAll
    static void stopContainers() {
        POSTGRES.stop();
        POSTGRES.close();

        KAFKA.stop();
        KAFKA.close();
    }

    @DynamicPropertySource
    static void configureProperties(final DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
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

    protected <T> T readResourceAsInputStream(
            final String resourcePath,
            final Function<InputStreamReader, T> converter) {
        try (final var resource = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            assert resource != null;

            try (final var content = new InputStreamReader(resource)) {
                return converter.apply(content);

            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected JsonNode readResourceAsJson(final String resourcePath) {
        return readResourceAsInputStream(
                resourcePath,
                inputStream -> {
                    try {
                        return jsonMapper.readTree(inputStream);
                    } catch (final IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    protected String readResourceAsString(final String resourcePath) {
        return readResourceAsInputStream(
                resourcePath,
                inputStream -> {
                    try {
                        return inputStream.readAllAsString();
                    } catch (final IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    protected void waitFor(final long amount, final TimeUnit timeUnit) {
        try (final var executor = Executors.newSingleThreadScheduledExecutor()) {
            executor.schedule(DungeonWalkerEngineStepsDef.DoNothing::new, amount, timeUnit);
        }
    }

    protected static class DoNothing implements Runnable {
        @Override
        public void run() {
            // Do nothing
        }
    }

}
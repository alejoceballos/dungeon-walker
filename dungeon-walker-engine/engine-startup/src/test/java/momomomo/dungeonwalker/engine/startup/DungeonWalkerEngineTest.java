package momomomo.dungeonwalker.engine.startup;

import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.contract.client.ClientRequestProto.ClientRequest;
import momomomo.dungeonwalker.contract.client.ConnectionProto.Connection;
import momomomo.dungeonwalker.contract.client.DirectionProto;
import momomomo.dungeonwalker.contract.client.MovementProto.Movement;
import momomomo.dungeonwalker.engine.domain.model.walker.moving.Direction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DungeonWalkerEngineTest {

    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:18-alpine"))
            .withDatabaseName("postgres")
            .withUsername("postgres")
            .withPassword("postgres")
            .withExposedPorts(5432)
            .withCopyFileToContainer(
                    MountableFile.forClasspathResource("init-db.sql"),
                    "/docker-entrypoint-initdb.d/");

    static final KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("apache/kafka:4.1.1"))
            .withExposedPorts(9092);

    @Autowired
    private TestKafkaProducer testKafkaProducer;

    @BeforeAll
    static void startContainers() {
        postgres.setPortBindings(List.of("5432:5432"));
        postgres.start();

        kafka.setPortBindings(List.of("9092:9092"));
        kafka.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    public void testEngineConsumesTopicAndProcessesMessage() {
        final var clientId = "user-id";

        final var connection = ClientRequest.newBuilder()
                .setClientId(clientId)
                .setConnection(Connection.newBuilder().build())
                .build();

        testKafkaProducer
                .produce(clientId, connection)
                .thenAccept(result ->
                        log.info("---> [TEST] - Connection result: {}", result))
                .exceptionally(ex -> {
                    log.error("---> [TEST] - Connection error: {}", ex.getMessage(), ex);
                    return null;
                });

        await().atMost(5, TimeUnit.SECONDS);

        Arrays.stream(Direction.values())
                .map(Direction::getAcronym)
                .forEach(direction -> {
                    final var movement = ClientRequest.newBuilder()
                            .setClientId(clientId)
                            .setMovement(
                                    Movement.newBuilder()
                                            .setDirection(DirectionProto.Direction.valueOf(direction))
                                            .build())
                            .build();
                    testKafkaProducer
                            .produce(clientId, movement)
                            .thenAccept(result ->
                                    log.info("---> [TEST] - Move {} result: {}", direction, result))
                            .exceptionally(ex -> {
                                log.error("---> [TEST] - Move {} error: {}", direction, ex.getMessage(), ex);
                                return null;
                            });

                    await().atMost(5, TimeUnit.SECONDS);
                });

        await()
                .atMost(1, TimeUnit.HOURS)
                .untilAsserted(() -> assertThat(false).isTrue());
    }

}
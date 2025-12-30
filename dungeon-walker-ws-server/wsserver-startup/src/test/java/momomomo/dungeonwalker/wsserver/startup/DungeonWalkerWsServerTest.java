package momomomo.dungeonwalker.wsserver.startup;

import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.contract.client.ClientRequestProto.ClientRequest;
import momomomo.dungeonwalker.contract.client.DirectionProto;
import momomomo.dungeonwalker.contract.engine.CoordinatesProto.Coordinates;
import momomomo.dungeonwalker.contract.engine.EngineMessageProto.EngineMessage;
import momomomo.dungeonwalker.contract.engine.WalkersPositionsProto.WalkersPositions;
import momomomo.dungeonwalker.wsserver.domain.input.Direction;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.util.MultiValueMap;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@Slf4j
@DirtiesContext
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DungeonWalkerWsServerTest {

    static final KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("apache/kafka:4.1.1"))
            .withExposedPorts(9092);

    @LocalServerPort
    private Integer port;

    @Autowired
    private KafkaConsumer<String, ClientRequest> testKafkaConsumer;

    @Autowired
    private TestKafkaProducer testKafkaProducer;

    @Autowired
    private TestWebSocketHandler testWsHandler;

    private final WebSocketClient wsClient = new StandardWebSocketClient();

    @BeforeAll
    static void startContainers() {
        kafka.setPortBindings(List.of("9092:9092"));
        kafka.start();
    }

    @Test
    public void testServerReceivesWebSocketMessageAndSendToTopic() {
        final var invalidData = """
                {
                    "type": "identity",
                    "data": {}
                }
                """;

        final var move = """
                {
                    "type": "movement",
                    "data": {
                        "direction": "%s"
                    }
                }
                """;

        final var directions = new ArrayList<>(List.of(DirectionProto.Direction.values()));
        directions.remove(DirectionProto.Direction.UNRECOGNIZED);
        directions.remove(DirectionProto.Direction.INVALID);

        consumerPoll(directions);

        wsClient.execute(
                        testWsHandler,
                        new WebSocketHttpHeaders(new HttpHeaders(MultiValueMap.fromSingleValue(
                                Map.of(HttpHeaders.AUTHORIZATION, "Basic dXNlcjE6cGFzc3dvcmQx")))),
                        URI.create("ws://localhost:" + port + "/ws-endpoint"))
                .thenAccept(session -> {
                    log.info("---> [TEST] - WS Client WebSocket connection succeeded");

                    waitSeconds(5);
                    consumerPoll(directions);

                    try {
                        session.sendMessage(new TextMessage(invalidData));

                        Arrays.stream(Direction.values()).forEach(direction -> {
                            waitSeconds(3);

                            try {
                                session.sendMessage(new TextMessage(move.formatted(direction.name())));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .exceptionally(ex -> {
                    log.error("---> [TEST - WS Client] Error connecting or sending message: {}", ex.getMessage());
                    return null;
                });

        waitSeconds(5);

        while (!directions.isEmpty()) {
            consumerPoll(directions);
            waitSeconds(1);
        }
    }

    @Test
    public void testServerReceivesEngineMessageAndSendToClient() throws ExecutionException, InterruptedException {
        wsClient.execute(
                testWsHandler,
                new WebSocketHttpHeaders(new HttpHeaders(MultiValueMap.fromSingleValue(
                        Map.of(HttpHeaders.AUTHORIZATION, "Basic dXNlcjE6cGFzc3dvcmQx")))),
                URI.create("ws://localhost:" + port + "/ws-endpoint")).get();

        waitSeconds(5);

        IntStream.of(1, 2, 3, 4, 5).forEach(counter -> {
            final var message = EngineMessage
                    .newBuilder()
                    .setWalkerPositions(createWalkersPositions(counter))
                    .build();

            testKafkaProducer.produce(message).exceptionally(ex -> {
                throw new RuntimeException(ex);
            });
        });

        await()
                .atMost(60, TimeUnit.SECONDS)
                .untilAsserted(() -> assertThat(false).isTrue());
    }

    private WalkersPositions createWalkersPositions(final int counter) {
        final var builder = WalkersPositions.newBuilder();

        IntStream
                .of(1, 2, 3, 4, 5)
                .forEach(id -> builder
                        .putCoordinatesByWalkerId(
                                "Walker " + id,
                                Coordinates
                                        .newBuilder()
                                        .setX(counter + id)
                                        .setY(counter + id)
                                        .build()));

        return builder.build();
    }

    private void waitSeconds(final long seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (final InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(ie);
        }
    }

    private void consumerPoll(ArrayList<DirectionProto.Direction> directions) {
        log.info("---> [TEST Poll] - Consumer is polling");
        final var records = testKafkaConsumer.poll(Duration.ofMillis(200));

        testKafkaConsumer.commitAsync((map, ex) -> {
            if (ex != null) {
                throw new RuntimeException(ex);
            }

            map.forEach((key, value) ->
                    log.info("---> [TEST Poll] - Committed offset {} for partition {}",
                            value.offset(), key.partition()));
        });

        log.info("---> [TEST Poll] - Poll count is: {}", records.count());

        for (final var record : records) {
            log.info("---> [TEST Poll] - Record: {}", record);

            if (record.value().hasMovement()) {
                directions.remove(record.value().getMovement().getDirection());
                log.info("---> [TEST Poll] - Removing {} from list", record.value().getMovement().getDirection());
            }
        }

        log.info("---> [TEST Poll] - Remaining: {}", directions);
    }

}
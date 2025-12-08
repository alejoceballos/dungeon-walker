package momomomo.dungeonwalker.wsserver.startup;

import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.wsserver.domain.input.Direction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
    private TestKafkaConsumer consumer;

    @Autowired
    private TestWebSocketHandler testWsHandler;

    private final WebSocketClient wsClient = new StandardWebSocketClient();

    @BeforeAll
    static void startContainers() {
        kafka.setPortBindings(List.of("9092:9092"));
        kafka.start();
    }

    @BeforeEach
    public void setUp() {
        consumer.emptyPayloads();
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

        wsClient
                .execute(
                        testWsHandler,
                        new WebSocketHttpHeaders(
                                new HttpHeaders(
                                        MultiValueMap.fromSingleValue(
                                                Map.of(HttpHeaders.AUTHORIZATION, "Basic dXNlcjE6cGFzc3dvcmQx")))),
                        URI.create("ws://localhost:" + port + "/ws-endpoint"))
                .thenAccept(session -> {
                    log.info("---> [TEST] - WS Client WebSocket connection succeeded");

                    try {
                        session.sendMessage(new TextMessage(invalidData));

                        Arrays.stream(Direction.values()).forEach(direction -> {
                            await().atMost(10, TimeUnit.SECONDS);

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

        var index = 0;
        while (true) {
            try {
                TimeUnit.SECONDS.sleep(10);

                if (consumer.getPayloads().size() > index) {
                    final var payload = consumer.getPayloads().get(index++);
                    log.info("-------------------> {} <-------------------", payload);
                }

            } catch (final InterruptedException ie) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(ie);
            }

        }

//        await()
//                .atMost(3600, TimeUnit.SECONDS)
//                .until(() -> !consumer.getPayloads().isEmpty());
//
//        await()
//                .atMost(1, TimeUnit.HOURS)
//                .untilAsserted(() -> assertThat(false).isTrue());
    }
}
package momomomo.dungeonwalker.wsserver.startup;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
        final var data = """
                {
                    "type": "identity",
                    "data": {
                        "id": "whatever",
                        "name": "Alejo"
                    }
                }
                """;

        wsClient
                .execute(testWsHandler, "ws://localhost:" + port + "/ws-endpoint")
                .thenAccept(session -> {
                    log.info("---> [TEST] - WS Client WebSocket connection succeeded");

                    try {
                        session.sendMessage(new TextMessage(data));

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .exceptionally(ex -> {
                    log.error("---> [TEST - WS Client] Error connecting or sending message: {}", ex.getMessage());
                    return null;
                });

        await()
                .atMost(1, TimeUnit.HOURS)
                .untilAsserted(() -> assertThat(false).isTrue());

//        await()
//                .atMost(3600, TimeUnit.SECONDS)
//                .until(() -> !consumer.getPayloads().isEmpty());
//
//        final var expected = AddClientWalkerProto.AddClientWalker.newBuilder().setId("whatever").build();
//
//        assertThat(consumer.getPayloads()).containsExactly(expected);
    }
}
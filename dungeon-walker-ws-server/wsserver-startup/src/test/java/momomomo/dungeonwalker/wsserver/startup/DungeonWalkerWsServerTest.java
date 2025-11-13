package momomomo.dungeonwalker.wsserver.startup;

import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.clientrequest.AddClientWalkerProto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
@EmbeddedKafka(
        partitions = 1,
        brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"}
)
class DungeonWalkerWsServerTest {

    @LocalServerPort
    private Integer port;

    @Autowired
    private TestKafkaConsumer consumer;

    private final WebSocketClient wsClient = new StandardWebSocketClient();

    @BeforeEach
    public void setUp() {
        consumer.emptyPayloads();
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    public void testBasicKafkaProducerAndConsumer() throws Exception {
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
                .execute(new TextWebSocketHandler(), "ws://localhost:" + port + "/ws-endpoint")
                .thenAccept(session -> {
                    log.info("[TEST] - WS Client WebSocket connection succeeded");

                    try {
                        session.sendMessage(new TextMessage(data));

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .exceptionally(ex -> {
                    log.error("[TEST - WS Client] Error connecting or sending message: {}", ex.getMessage());
                    return null;
                });

        await()
                .atMost(3600, TimeUnit.SECONDS)
                .until(() -> !consumer.getPayloads().isEmpty());

        final var expected = AddClientWalkerProto.AddClientWalker.newBuilder().setId("whatever").build();

        assertThat(consumer.getPayloads()).containsExactly(expected);
    }
}
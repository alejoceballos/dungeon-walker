package momomomo.dungeonwalker.wsserver.startup.steps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.contract.client.ClientRequestProto.ClientRequest;
import momomomo.dungeonwalker.contract.engine.EngineMessageProto.EngineMessage;
import momomomo.dungeonwalker.wsserver.startup.DungeonWalkerWsServerIntegrationTests;
import momomomo.dungeonwalker.wsserver.startup.support.TestClientWebSocketHandler;
import momomomo.dungeonwalker.wsserver.startup.support.TestKafkaProducer;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class DungeonWalkerWsServerStepsDef extends DungeonWalkerWsServerIntegrationTests {

    private static final long WAIT_VALUE = 100;
    private static final TimeUnit WAIT_UNIT = MILLISECONDS;

    private static final String TO_CLIENT_FILE = "data/outbound/user/%s.json";
    private static final String TO_ENGINE_FILE = "data/outbound/engine/%s.json";
    private static final String FROM_CLIENT_FILE = "data/inbound/user/%s.json";
    private static final String FROM_ENGINE_FILE = "data/inbound/engine/%s.json";

    @Autowired
    private KafkaConsumer<String, ClientRequest> testKafkaConsumer;

    @Autowired
    private TestKafkaProducer testKafkaProducer;

    @Autowired
    private TestClientWebSocketHandler clientWsHandler;

    private final WebSocketClient wsClient = new StandardWebSocketClient();
    private final ConcurrentMap<String, ClientContext> clientsContext = new ConcurrentHashMap<>();

    @Before
    public void scenarioSetUp() {
        clientWsHandler.getState().clear();
    }

    @Given("user {string} sends a connection request to the server")
    public void clientConnects(final String userLabel) {
        wsClient
                .execute(clientWsHandler, "ws://localhost:" + port + "/ws-endpoint")
                .thenAccept(wsSession -> {
                    clientsContext.put(userLabel, new ClientContext());
                    final var context = clientsContext.get(userLabel);
                    context.setSession(wsSession);
                });
    }

    @When("the server establishes a connection with user {string}")
    public void assertServerEstablishesConnection(final String userLabel) {
        waitFor(WAIT_VALUE, WAIT_UNIT);

        final var wsClientSession = clientWsHandler
                .getState()
                .afterConnectionEstablished()
                .stream()
                .filter(wsSession -> {
                    final var context = clientsContext.get(userLabel);
                    return context.getSession().equals(wsSession);
                });

        assertThat(wsClientSession).hasSize(1);
    }

    @Then("user {string} receives the following message(s) from the server:")
    public void clientReceiveMessage(final String userLabel, final List<String> messages) throws JsonProcessingException {
        waitFor(WAIT_VALUE, WAIT_UNIT);

        final var receivedMessages = clientWsHandler
                .getState()
                .handleTextMessage()
                .stream()
                .filter(pair -> {
                    final var session = clientsContext.get(userLabel).getSession();
                    return session.equals(pair.getLeft());
                })
                .map(Pair::getRight)
                .toList();

        for (var index = 0; index < messages.size(); index++) {
            final var filePath = TO_CLIENT_FILE
                    .formatted(messages.get(index))
                    .replace(":", "")
                    .replace(" ", "-");
            final var expectedJson = readResourceAsJson(filePath);

            final var messageToCheckIndex = receivedMessages.size() - messages.size() + index;
            final var actualJson = jsonMapper.readTree(receivedMessages.get(messageToCheckIndex));

            assertThat(actualJson).isEqualTo(expectedJson);
        }
    }

    @And("user {string} sends a(n) {string} message to the server")
    public void clientSendsMessage(final String userLabel, final String message) throws IOException {
        waitFor(WAIT_VALUE, WAIT_UNIT);

        final var filePath = FROM_CLIENT_FILE
                .formatted(message)
                .replace(":", "")
                .replace(" ", "-");
        final var tokenResponse = requestToken();
        final var token = jsonMapper.readTree(tokenResponse).get("access_token").asText();
        final var json = readResourceAsString(filePath).replace("{{ACCESS_TOKEN}}", token);

        clientsContext
                .get(userLabel)
                .getSession()
                .sendMessage(new TextMessage(json));
    }

    @Then("the server sends a {string} request to the engine")
    public void serverSendsMessage(final String message) throws InvalidProtocolBufferException, JsonProcessingException {
        waitFor(WAIT_VALUE, WAIT_UNIT);

        final List<ConsumerRecord<String, ClientRequest>> messagesToEngine = new ArrayList<>();

        for (final var consumerRecord : testKafkaConsumer.poll(Duration.ofMillis(500))) {
            messagesToEngine.add(consumerRecord);
        }

        testKafkaConsumer.commitSync();

        assertThat(messagesToEngine).hasSize(1);

        final var protoAsJsonString = JsonFormat.printer().print(messagesToEngine.getLast().value());
        final var actualJson = jsonMapper.readTree(protoAsJsonString);


        final var filePath = TO_ENGINE_FILE
                .formatted(message)
                .replace(":", "")
                .replace(" ", "-");
        final var expectedJson = readResourceAsJson(filePath);

        assertThat(actualJson).isEqualTo(expectedJson);
    }

    @When("the engine sends a(n) {string} message to the server")
    public void produceMessageToEngineOutboundTopic(final String message) throws InvalidProtocolBufferException {
        final var filePath = FROM_ENGINE_FILE
                .formatted(message)
                .replace(":", "")
                .replace(" ", "-");
        final var json = readResourceAsString(filePath);

        final var engineMessageBuilder = EngineMessage.newBuilder();
        JsonFormat
                .parser()
                .ignoringUnknownFields()
                .merge(json, engineMessageBuilder);

        final var engineMessage = engineMessageBuilder.build();

        testKafkaProducer
                .produce(engineMessage)
                .exceptionally(ex -> {
                    throw new RuntimeException(ex);
                });
    }

    @NoArgsConstructor
    private static class ClientContext {

        @Setter
        @Getter
        private WebSocketSession session;

    }

}

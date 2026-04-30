package momomomo.dungeonwalker.wsserver.startup.steps;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.contract.client.ClientRequestProto;
import momomomo.dungeonwalker.contract.engine.EngineMessageProto.EngineMessage;
import momomomo.dungeonwalker.wsserver.startup.DungeonWalkerWsServerIntegrationTests;
import momomomo.dungeonwalker.wsserver.startup.support.TestClientWebSocketHandler;
import momomomo.dungeonwalker.wsserver.startup.support.TestKafkaProducer;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.upperCase;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class DungeonWalkerWsServerStepsDef extends DungeonWalkerWsServerIntegrationTests {

    @Autowired
    private KafkaConsumer<String, ClientRequestProto.ClientRequest> testKafkaConsumer;

    @Autowired
    private TestKafkaProducer testKafkaProducer;

    @Autowired
    private TestClientWebSocketHandler clientWsHandler;

    private final WebSocketClient wsClient = new StandardWebSocketClient();
    private final Map<String, LoggedUser> loggedUsers = new HashMap<>();

    private final List<List<ConsumerRecord<String, ClientRequestProto.ClientRequest>>> messagesToEngineByProcess = new ArrayList<>();
    private final List<String> messagesSentByClient = new ArrayList<>();
    private final List<String> messagesSentByEngine = new ArrayList<>();

    @Before
    public void scenarioSetUp() {
        clientWsHandler.getState().clear();
        loggedUsers.clear();
        messagesToEngineByProcess.clear();
        messagesSentByClient.clear();
        messagesSentByEngine.clear();
    }

    @Given("a user {string} with password {string} as client {string}")
    public void storeUserCredentials(
            final String user,
            final String password,
            final String clientId) {
        final var credentialsToEncode = "%s:%s".formatted(user, password);
        final var encodedCredentials = Base64.getEncoder().encodeToString(credentialsToEncode.getBytes());
        loggedUsers.put(clientId, new LoggedUser(user, password, encodedCredentials));
    }

    @Given("the client {string} sends a connection request to the WebSocket server")
    public void sendWebSocketConnectionRequest(final String clientId) {
        final var encodedCredentials = "Basic %s".formatted(loggedUsers.get(clientId).getEncodedCredentials());

        wsClient
                .execute(
                        clientWsHandler,
                        new WebSocketHttpHeaders(
                                new HttpHeaders(MultiValueMap.fromSingleValue(
                                        Map.of(HttpHeaders.AUTHORIZATION, encodedCredentials)))),
                        URI.create("ws://localhost:" + port + "/ws-endpoint"))
                .thenAccept(loggedUsers.get(clientId)::setSession);
    }

    @When("the WebSocket server process the messages")
    public void serverProcess() {
        final List<ConsumerRecord<String, ClientRequestProto.ClientRequest>> messagesToEngine = new ArrayList<>();

        for (final var consumerRecord : testKafkaConsumer.poll(Duration.ofMillis(500))) {
            messagesToEngine.add(consumerRecord);
        }

        messagesToEngineByProcess.add(messagesToEngine);
        testKafkaConsumer.commitSync();
    }

    @Then("the WebSocket server establishes {int} connection(s) with client(s)")
    public void checkClientAfterConnectionEstablished(final int count) {
        assertThat(clientWsHandler.getState().afterConnectionEstablished())
                .as("Expected a connection to be established")
                .hasSize(count);
    }

    @Then("the WebSocket server sends {int} request(s) to the Engine")
    public void sendClientRequest(final int expectedCount) {
        assertThat(messagesToEngineByProcess.getLast())
                .as("Expected %d messages to be sent to the engine topic, but got %d",
                        expectedCount,
                        messagesToEngineByProcess.getLast().size())
                .hasSize(expectedCount);
    }

    @Then("the WebSocket server request, started by client {string}, to the Engine, should be a(n) {string} request")
    public void stepCheckClientRequestType(final String clientId, final String requestType) {
        checkClientRequestType(clientId, requestType);
    }

    @Given("the client {string} sends a disconnection request to the WebSocket server")
    public void sendWebSocketDisconnectionRequest(final String clientId) throws IOException {
        loggedUsers.get(clientId).getSession().close();
    }

    @Then("the client {string} should disconnect from the WebSocket server")
    public void checkClientAfterConnectionClosed(final String clientId) {
        assertThat(getWebSocketSession(clientId, clientWsHandler.getState().afterConnectionClosed()))
                .as("Expected a connection to be closed")
                .isNotNull();
    }

    @Given("the following client JSON message:")
    public void storeMessagesSentByClient(final String messageBody) {
        messagesSentByClient.add(messageBody);
    }

    @And("the client {string} sends the message to the WebSocket server")
    public void clientSendMessageToTheWebSocketServer(final String clientId) throws IOException {
        loggedUsers
                .get(clientId)
                .getSession()
                .sendMessage(new TextMessage(messagesSentByClient.getLast()));
    }

    @Given("the following Engine Protobuf message as JSON:")
    public void storeMessageSentByEngine(final String messageBodyAsJson) {
        messagesSentByEngine.add(messageBodyAsJson);
    }

    @When("the Engine sends the Protobuf message(s) to the WebSocket server")
    public void theServerSendsTheProtobufMessageToTheClient() throws InvalidProtocolBufferException {
        for (final String message : messagesSentByEngine) {
            final var engineMessageBuilder = EngineMessage.newBuilder();

            JsonFormat
                    .parser()
                    .ignoringUnknownFields()
                    .merge(message, engineMessageBuilder);

            final var engineMessage = engineMessageBuilder.build();

            testKafkaProducer
                    .produce(engineMessage)
                    .exceptionally(ex -> {
                        throw new RuntimeException(ex);
                    });
        }
    }

    @Then("the client {string} should receive {int} {string} message(s) from the Engine")
    public void theClientShouldReceiveTheProtobufMessage(
            final String clientId,
            final int expectedCount,
            final String messageType) {
        final var messageCount = clientWsHandler
                .getState()
                .handleTextMessage()
                .stream()
                .filter(pair -> loggedUsers
                        .get(clientId)
                        .getSession()
                        .equals(pair.getLeft()))
                .map(Pair::getRight)
                .filter(message -> message.contains(messageType))
                .count();

        assertThat(messageCount)
                .as("Expected client %s to receive %d message(s) from the engine, but got %d",
                        clientId,
                        expectedCount,
                        messageCount)
                .isEqualTo(expectedCount);
    }

    @Then("after {long} {string}(s)")
    public void waitFor(final long amount, final String timeUnit) {
        final var validTimeUnit = upperCase(timeUnit).endsWith("S") ? upperCase(timeUnit) : upperCase(timeUnit) + "S";

        try (final var executor = Executors.newSingleThreadScheduledExecutor()) {
            executor.schedule(DoNothing::new, amount, TimeUnit.valueOf(validTimeUnit));
        }
    }

    private void checkClientRequestType(final String clientId, final String requestType) {
        final var clientRequest = messagesToEngineByProcess
                .getLast()
                .stream()
                .filter(consumerRecord -> consumerRecord
                        .value()
                        .getClientId()
                        .equals(loggedUsers.get(clientId).getUser()))
                .findAny()
                .orElseThrow(() -> new RuntimeException("No request found for client " + clientId))
                .value();

        assertThat(clientRequest.getDataCase().name().toLowerCase())
                .as("Expected client %s request to be a %s request", clientId, requestType)
                .isEqualTo(requestType.toLowerCase());
    }

    private <T> WebSocketSession getWebSocketSession(final String clientId, final CopyOnWriteArrayList<Pair<WebSocketSession, T>> sessions) {
        return filterStreamByClientId(clientId, sessions.stream().map(Pair::getLeft));
    }

    private WebSocketSession filterStreamByClientId(final String clientId, final Stream<WebSocketSession> sessions) {
        return sessions
                .filter(loggedUsers.get(clientId).getSession()::equals)
                .findAny()
                .orElseThrow(() -> new RuntimeException("No session found for client " + clientId));
    }

    @Getter
    @RequiredArgsConstructor
    private static class LoggedUser {

        private final String user;
        private final String password;
        private final String encodedCredentials;

        @Setter
        private WebSocketSession session;

    }

    private static class DoNothing implements Runnable {
        @Override
        public void run() {
            // Do nothing
        }
    }

}

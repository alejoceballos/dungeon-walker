package momomomo.dungeonwalker.engine.startup.steps;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.contract.client.ClientRequestProto;
import momomomo.dungeonwalker.contract.engine.EngineMessageProto.EngineMessage;
import momomomo.dungeonwalker.engine.startup.DungeonWalkerEngineIntegrationTests;
import momomomo.dungeonwalker.engine.startup.support.TestKafkaProducer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.apache.commons.lang3.StringUtils.upperCase;
import static org.apache.commons.lang3.Strings.CS;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class DungeonWalkerEngineStepsDef extends DungeonWalkerEngineIntegrationTests {

    private static final String LABEL = "===> [TEST - Step Def]";

    private static final long JUST_A_BIT = 500;
    private static final TimeUnit WAIT_UNIT = MILLISECONDS;

    private static final String FROM_USER_PATH = "data/inbound/user/";
    private static final String TO_USER_PATH = "data/outbound/user/";

    @Autowired
    private TestKafkaProducer testKafkaProducer;

    @Autowired
    private KafkaConsumer<String, EngineMessage> testKafkaConsumer;

    @Given("the engine receives a client request to {string}( again)")
    public void engineReceivesMessage(final String message) throws InvalidProtocolBufferException {
        log.info("{} Engine receives a client request to {}", LABEL, message);

        waitFor(JUST_A_BIT, WAIT_UNIT);

        final var json = readResourceAsString(messageToFilePath(FROM_USER_PATH, message));
        final var clientRequestBuilder = ClientRequestProto.ClientRequest.newBuilder();

        JsonFormat
                .parser()
                .ignoringUnknownFields()
                .merge(json, clientRequestBuilder);

        final var clientRequest = clientRequestBuilder.build();

        testKafkaProducer
                .produce(clientRequest.getClientId(), clientRequest)
                .exceptionally(ex -> {
                    throw new RuntimeException(ex);
                });
    }

    @Then("the engine sends exactly the following message(s) to the client:")
    public void engineSendsExactMessages(final DataTable messages) {
        log.info("{} Engine sends exactly the following message(s) to the client: {}", LABEL, messages.asList(String.class));

        waitFor(JUST_A_BIT, WAIT_UNIT);

        final List<ConsumerRecord<String, EngineMessage>> actualMessages = pollMessagesFromKafkaTopic();
        final var expectedMessages = messages.asList(String.class);

        assertThat(actualMessages).hasSize(expectedMessages.size());

        assertActualMessagesContainsExpectedOnes(actualMessages, expectedMessages);
    }

    @Then("the engine sends at least the following message(s) to the client:")
    public void engineSendsAltLeastMessages(final DataTable messages) {
        log.info("{} Engine sends alt least the following message(s) to the client: {}", LABEL, messages.asList(String.class));

        waitFor(JUST_A_BIT, WAIT_UNIT);

        final List<ConsumerRecord<String, EngineMessage>> actualMessages = pollMessagesFromKafkaTopic();
        final var expectedMessages = messages.asList(String.class);

        assertThat(actualMessages).hasSizeGreaterThanOrEqualTo(expectedMessages.size());

        assertActualMessagesContainsExpectedOnes(actualMessages, expectedMessages);
    }

    @Then("the engine sends a(n) {string} message to the client")
    public void engineSendsMessage(final String message) {
        log.info("{} Engine sends a(n) {} message to the client", LABEL, message);

        waitFor(JUST_A_BIT, WAIT_UNIT);

        final List<ConsumerRecord<String, EngineMessage>> messagesToClient = pollMessagesFromKafkaTopic();

        assertThat(messagesToClient).hasSize(1);

        final var actualProto = messagesToClient.getLast().value();
        final var expectedProto = messageToProto(message);

        assertThat(actualProto).isEqualTo(expectedProto);

    }

    @And("(It ends )after {long} {string}")
    public void afterSomeTime(final long value, final String unit) {
        log.info("{} Wait for {} {}", LABEL, value, unit);

        final var timeUnit = CS.endsWith(upperCase(unit), "S") ? upperCase(unit) : upperCase(unit) + "S";

        waitFor(value, TimeUnit.valueOf(timeUnit));

        log.info("{} Finished waiting for {} {}", LABEL, value, unit);
    }

    private @NonNull List<ConsumerRecord<String, EngineMessage>> pollMessagesFromKafkaTopic() {
        final List<ConsumerRecord<String, EngineMessage>> messagesToClient = new ArrayList<>();

        for (final var consumerRecord : testKafkaConsumer.poll(Duration.ofMillis(500))) {
            messagesToClient.add(consumerRecord);
        }

        testKafkaConsumer.commitSync();
        return messagesToClient;
    }

    private String messageToFilePath(final String pathPrefix, final String message) {
        return pathPrefix
                .concat(message)
                .replace(": ", "/")
                .replace(":", "/")
                .replace("  ", "-")
                .replace(" ", "-")
                .concat(".json");
    }

    private @NonNull EngineMessage messageToProto(final String message) {
        final var filePath = messageToFilePath(TO_USER_PATH, message);
        final var expectedJson = readResourceAsJson(filePath);
        final var expectedProtoBuilder = EngineMessage.newBuilder();

        try {
            JsonFormat
                    .parser()
                    .ignoringUnknownFields()
                    .merge(expectedJson.toString(), expectedProtoBuilder);
        } catch (final InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }

        return expectedProtoBuilder.build();
    }

    private void assertActualMessagesContainsExpectedOnes(
            final List<ConsumerRecord<String, EngineMessage>> actualMessages,
            final List<String> expectedMessages
    ) {
        final var actualProtos = actualMessages
                .stream()
                .map(ConsumerRecord::value)
                .toList();

        expectedMessages
                .stream()
                .map(this::messageToProto)
                .forEach(expectedProto ->
                        assertThat(actualProtos)
                                .describedAs("Expected message not found in set: %s", expectedProto)
                                .contains(expectedProto));
    }

}

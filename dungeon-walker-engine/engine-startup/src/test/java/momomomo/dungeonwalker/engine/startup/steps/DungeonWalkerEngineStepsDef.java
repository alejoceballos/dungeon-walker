package momomomo.dungeonwalker.engine.startup.steps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.contract.client.ClientRequestProto;
import momomomo.dungeonwalker.contract.engine.EngineMessageProto.EngineMessage;
import momomomo.dungeonwalker.engine.startup.DungeonWalkerEngineIntegrationTests;
import momomomo.dungeonwalker.engine.startup.support.TestKafkaProducer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class DungeonWalkerEngineStepsDef extends DungeonWalkerEngineIntegrationTests {

    private static final long WAIT_VALUE = 100;
    private static final TimeUnit WAIT_UNIT = MILLISECONDS;

    private static final String FROM_USER_FILE = "data/inbound/user/%s.json";
    private static final String TO_USER_FILE = "data/outbound/user/%s.json";

    @Autowired
    private TestKafkaProducer testKafkaProducer;

    @Autowired
    private KafkaConsumer<String, EngineMessage> testKafkaConsumer;

    @Given("the engine receives a client request to {string}( again)")
    public void engineReceivesMessage(final String message) throws InvalidProtocolBufferException {
        final var filePath = FROM_USER_FILE
                .formatted(message)
                .replace(":", "")
                .replace(" ", "-");

        final var json = readResourceAsString(filePath);

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

    @Then("the engine sends a(n) {string} message to the client")
    public void engineSendsMessage(
            final String message
    ) throws InvalidProtocolBufferException, JsonProcessingException {
        waitFor(WAIT_VALUE, WAIT_UNIT);

        final List<ConsumerRecord<String, EngineMessage>> messagesToClient = new ArrayList<>();

        for (final var consumerRecord : testKafkaConsumer.poll(Duration.ofMillis(500))) {
            messagesToClient.add(consumerRecord);
        }

        testKafkaConsumer.commitSync();

        assertThat(messagesToClient).hasSize(1);

        final var protoAsJsonString = JsonFormat.printer().print(messagesToClient.getLast().value());
        final var actualJson = jsonMapper.readTree(protoAsJsonString);

        final var filePath = TO_USER_FILE
                .formatted(message)
                .replace(":", "")
                .replace(" ", "-");
        final var expectedJson = readResourceAsJson(filePath);

        assertThat(actualJson).isEqualTo(expectedJson);

    }
}

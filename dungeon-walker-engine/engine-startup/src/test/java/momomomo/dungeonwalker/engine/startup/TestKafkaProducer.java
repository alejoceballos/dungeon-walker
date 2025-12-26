package momomomo.dungeonwalker.engine.startup;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.contract.client.ClientRequestProto.ClientRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class TestKafkaProducer {

    private final KafkaTemplate<@NonNull String, @NonNull ClientRequest> testKafkaTemplate;
    private final String topic;

    public TestKafkaProducer(
            final KafkaTemplate<@NonNull String, @NonNull ClientRequest> testKafkaTemplate,
            @Value("${kafka.topic.game-engine.inbound}") String topic) {
        this.testKafkaTemplate = testKafkaTemplate;
        this.topic = topic;
    }

    public CompletableFuture<SendResult<@NonNull String, @NonNull ClientRequest>> produce(
            final String messageId,
            final ClientRequest payload) {
        log.info("---> [TEST KAFKA - Producer] Sending payload");
        return testKafkaTemplate.send(topic, messageId, payload);
    }

}

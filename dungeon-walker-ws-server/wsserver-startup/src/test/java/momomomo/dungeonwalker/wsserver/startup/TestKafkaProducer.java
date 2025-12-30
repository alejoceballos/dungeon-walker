package momomomo.dungeonwalker.wsserver.startup;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.contract.engine.EngineMessageProto.EngineMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class TestKafkaProducer {

    private final KafkaTemplate<@NonNull String, @NonNull EngineMessage> testKafkaTemplate;
    private final String topic;

    public TestKafkaProducer(
            final KafkaTemplate<@NonNull String, @NonNull EngineMessage> testKafkaTemplate,
            @Value("${kafka.topic.game-engine.inbound}") String topic
    ) {
        this.testKafkaTemplate = testKafkaTemplate;
        this.topic = topic;
    }

    public CompletableFuture<SendResult<@NonNull String, @NonNull EngineMessage>> produce(
            final EngineMessage payload
    ) {
        return testKafkaTemplate.send(topic, payload.getDataCase().name(), payload);
    }

}

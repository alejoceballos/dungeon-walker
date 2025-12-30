package momomomo.dungeonwalker.engine.transport.outbound;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.contract.engine.EngineMessageProto.EngineMessage;
import momomomo.dungeonwalker.engine.domain.outbound.Sender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class KafkaProducer implements Sender<EngineMessage> {

    private final KafkaTemplate<@NonNull String, @NonNull EngineMessage> kafkaTemplate;
    private final String topic;

    public KafkaProducer(
            @Value("${kafka.topic.game-engine.outbound}") final String topic,
            final KafkaTemplate<@NonNull String, @NonNull EngineMessage> kafkaTemplate
    ) {
        log.debug("---> [OUTBOUND - Kafka Producer] Bean created. Topic \"{}\". Kafka template: \"{}\"", topic, kafkaTemplate);
        this.topic = topic;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void send(@NonNull final EngineMessage message) {
        log.debug("---> [OUTBOUND - Kafka Producer] Sending message \"{}\" to topic \"{}\"", message, topic);

        kafkaTemplate.send(topic, message.getDataCase().name(), message)
                .thenCompose(_ -> {
                    log.info("---> [OUTBOUND - Kafka Producer] Message sent successfully to topic \"{}\": {}", topic, message);
                    return CompletableFuture.completedFuture(null);
                }).exceptionally(ex -> {
                    log.error("---> [OUTBOUND - Kafka Producer] Error sending message to topic \"{}\": {}", topic, message, ex);
                    return null;
                });
    }

}

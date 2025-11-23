package momomomo.dungeonwalker.wsserver.transport.outbound;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.clientrequest.AddClientWalkerProto;
import momomomo.dungeonwalker.wsserver.domain.outbound.Sender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class KafkaProducer implements Sender<AddClientWalkerProto.AddClientWalker> {

    private final KafkaTemplate<@NonNull String, byte @NonNull []> kafkaTemplate;
    private final String topic;

    public KafkaProducer(
            @Value("${kafka.topic.outbound.game-engine}") final String topic,
            final KafkaTemplate<@NonNull String, byte @NonNull []> kafkaTemplate) {
        log.debug("---> [OUTBOUND - Kafka Producer] Bean created");
        this.topic = topic;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(@NonNull final AddClientWalkerProto.AddClientWalker message) {
        log.debug("---> [OUTBOUND - Kafka Producer] Sending message \"{}\"", message);

        kafkaTemplate.send(topic, message.getId(), message.toByteArray())
                .thenCompose(_ -> {
                    log.info("---> [OUTBOUND - Kafka Producer] Message sent successfully");
                    return CompletableFuture.completedFuture(null);
                }).exceptionally(ex -> {
                    log.error("---> [OUTBOUND - Kafka Producer] Error sending message", ex);
                    return null;
                });
    }

}

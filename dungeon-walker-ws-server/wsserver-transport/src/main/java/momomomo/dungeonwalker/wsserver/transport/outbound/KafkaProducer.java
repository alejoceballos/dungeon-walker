package momomomo.dungeonwalker.wsserver.transport.outbound;

import jakarta.annotation.Nonnull;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.contract.client.ClientRequestProto.ClientRequest;
import momomomo.dungeonwalker.wsserver.domain.outbound.SendResult;
import momomomo.dungeonwalker.wsserver.domain.outbound.Sender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

import static momomomo.dungeonwalker.wsserver.domain.outbound.SendStatus.FAILURE;
import static momomomo.dungeonwalker.wsserver.domain.outbound.SendStatus.SUCCESS;

@Slf4j
@Component
public class KafkaProducer implements Sender<ClientRequest> {

    private final KafkaTemplate<@NonNull String, @NonNull ClientRequest> kafkaTemplate;
    private final String topic;

    public KafkaProducer(
            @Value("${kafka.topic.outbound.game-engine}") final String topic,
            final KafkaTemplate<@NonNull String, @NonNull ClientRequest> kafkaTemplate) {
        log.debug("---> [OUTBOUND - Kafka Producer] Bean created");
        this.topic = topic;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Nonnull
    @Override
    public CompletableFuture<SendResult> send(@NonNull final ClientRequest message) {
        log.debug("---> [OUTBOUND - Kafka Producer] Sending message \"{}\"", message);

        return kafkaTemplate.send(topic, message.getClientId(), message)
                .thenCompose(_ -> {
                    log.info("---> [OUTBOUND - Kafka Producer] Message sent successfully: {}", message);
                    return CompletableFuture.completedFuture(new SendResult(
                            SUCCESS,
                            "Message sent successfully"));
                }).exceptionally(ex -> {
                    log.error("---> [OUTBOUND - Kafka Producer] Error sending message: {}", message, ex);
                    return new SendResult(FAILURE, ex.getMessage());
                });
    }

}

package momomomo.dungeonwalker.wsserver.transport.outbound;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.contract.client.ClientRequestProto.ClientRequest;
import momomomo.dungeonwalker.wsserver.domain.data.engine.output.EngineOutbound;
import momomomo.dungeonwalker.wsserver.domain.data.engine.output.SendResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static momomomo.dungeonwalker.wsserver.domain.data.engine.output.SendStatus.FAILURE;
import static momomomo.dungeonwalker.wsserver.domain.data.engine.output.SendStatus.SUCCESS;

@Slf4j
@Component
public class KafkaProducer implements EngineOutbound<ClientRequest> {

    private static final String LABEL = "---> [OUTBOUND - Kafka Producer]";

    private final KafkaTemplate<@NonNull String, @NonNull ClientRequest> kafkaTemplate;
    private final String topic;

    public KafkaProducer(
            @Value("${kafka.topic.game-engine.outbound}") final String topic,
            final KafkaTemplate<@NonNull String, @NonNull ClientRequest> kafkaTemplate) {
        log.debug("{} Bean created. Topic \"{}\". Kafka template: \"{}\"", LABEL, topic, kafkaTemplate);
        this.topic = topic;
        this.kafkaTemplate = kafkaTemplate;
    }

    @NonNull
    @Override
    public CompletableFuture<SendResult> send(@NonNull final ClientRequest message) {
        log.debug("{} Sending message \"{}\" to topic \"{}\"", LABEL, message, topic);

        return kafkaTemplate.send(topic, message.getClientId(), message)
                .thenCompose(_ -> {
                    log.info("{} Message sent successfully to topic \"{}\": {}", LABEL, topic, message);
                    return completedFuture(new SendResult(SUCCESS, "Message sent successfully"));
                }).exceptionally(ex -> {
                    log.error("{} Error sending message to topic \"{}\": {}", LABEL, topic, message, ex);
                    return new SendResult(FAILURE, ex.getMessage());
                });
    }

}

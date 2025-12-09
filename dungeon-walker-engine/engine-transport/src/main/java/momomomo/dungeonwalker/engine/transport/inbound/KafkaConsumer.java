package momomomo.dungeonwalker.engine.transport.inbound;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.contract.client.ClientRequestProto.ClientRequest;
import momomomo.dungeonwalker.engine.domain.handler.SelectableHandler;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumer {

    private final List<SelectableHandler<ClientRequest>> handlers;

    @KafkaListener(
            topics = "${kafka.topic.inbound.game-engine}",
            groupId = "${spring.kafka.consumer.group-id}",
            properties = {"${spring.kafka.consumer.auto-offset-reset}"}
    )
    public void consume(
            final ConsumerRecord<String, ClientRequest> record,
            final Acknowledgment ack
    ) {
        log.info("---> [INBOUND - Kafka Consumer] Message received: \"{}\":\"{}\"", record.key(), record.value());

        handlers.stream()
                .filter(handler -> handler.shouldHandle(record.value()))
                .findFirst()
                .map(handler -> handler.handle(record.value()))
                .orElse(new NoHandlerResult(
                        record.value(),
                        new IllegalArgumentException("No handler found for message")))
                .onSuccess(proto -> {
                    log.info("---> [INBOUND - Kafka Consumer] Message successful processed: \"{}\"/\"{}\"",
                            record.key(),
                            proto);
                    ack.acknowledge();
                })
                .onFailure((proto, throwable) -> {
                    log.error("---> [INBOUND - Kafka Consumer] Message processing failure: \"{}\"/\"{}\": {}",
                            record.key(),
                            proto,
                            throwable.getMessage());
                    ack.acknowledge();
                });
    }

}

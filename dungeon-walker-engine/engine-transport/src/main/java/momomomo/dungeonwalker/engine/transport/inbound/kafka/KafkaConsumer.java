package momomomo.dungeonwalker.engine.transport.inbound.kafka;

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

    private static final String LABEL = "---> [CONSUMER - Kafka]";

    private final List<SelectableHandler<ClientRequest>> handlers;

    @KafkaListener(
            topics = "${kafka.topic.game-engine.inbound}",
            groupId = "${spring.kafka.consumer.group-id}",
            properties = {"${spring.kafka.consumer.auto-offset-reset}"}
    )
    public void consume(
            final ConsumerRecord<String, ClientRequest> record,
            final Acknowledgment ack
    ) {
        log.info("{} Message received: \"{}\":\"{}\"", LABEL, record.key(), record.value());

        handlers.stream()
                .filter(handler -> handler.shouldHandle(record.value()))
                .findFirst()
                .map(handler -> handler.handle(record.value()))
                .orElse(new NoHandlerResult(
                        record.value(),
                        new IllegalArgumentException("No handler found for message")))
                .onSuccess(proto -> {
                    log.info("{} Message successful processed: \"{}\"/\"{}\"", LABEL, record.key(), proto);
                    ack.acknowledge();
                })
                .onFailure((proto, throwable) -> {
                    log.error("{} Message processing failure: \"{}\"/\"{}\": {}", LABEL, record.key(), proto, throwable.getMessage());
                    ack.acknowledge();
                });
    }

}

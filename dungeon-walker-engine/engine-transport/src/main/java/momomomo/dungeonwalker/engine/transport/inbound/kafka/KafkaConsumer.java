package momomomo.dungeonwalker.engine.transport.inbound.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.contract.client.ClientRequestProto.ClientRequest;
import momomomo.dungeonwalker.engine.domain.inbound.ClientInbound;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumer {

    private static final String LABEL = "---> [CONSUMER - Kafka]";

    private final ClientInbound<ClientRequest> inbound;

    @KafkaListener(
            topics = "${kafka.topic.game-engine.inbound}",
            groupId = "${spring.kafka.consumer.group-id}",
            properties = {"${spring.kafka.consumer.auto-offset-reset}"}
    )
    public void consume(
            final ConsumerRecord<String, ClientRequest> consumerRecord,
            final Acknowledgment ack
    ) {
        log.info("{} Message received: \"{}\":\"{}\"", LABEL, consumerRecord.key(), consumerRecord.value());

        inbound.handleMessage(consumerRecord.value());
        ack.acknowledge();
    }

}

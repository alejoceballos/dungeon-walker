package momomomo.dungeonwalker.wsserver.startup;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.contract.client.ClientRequestProto.ClientRequest;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TestKafkaConsumer {

    @Getter
    private final List<ClientRequest> payloads = new ArrayList<>();

    @KafkaListener(
            topics = "${kafka.topic.outbound.game-engine}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void receive(
            final ConsumerRecord<String, ClientRequest> consumerRecord,
            final Acknowledgment acknowledgment) {
        log.info("---> [KAFKA - Consumer] Received payload");

        try {
            payloads.add(consumerRecord.value());

        } catch (Exception e) {
            log.error("---> [KAFKA - Consumer] Error parsing message. Error: {}", e.getMessage());

        } finally {
            acknowledgment.acknowledge();
        }
    }

    public void emptyPayloads() {
        payloads.clear();
    }

}
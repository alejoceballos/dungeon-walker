package momomomo.dungeonwalker.wsserver.startup;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.clientrequest.AddClientWalkerProto;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TestKafkaConsumer {

    @Getter
    private final List<AddClientWalkerProto.AddClientWalker> payloads = new ArrayList<>();

    @KafkaListener(
            groupId = "dungeon-walker-ws-server",
            properties = {"auto.offset.reset:earliest"},
            topics = "${kafka.topic.outbound.game-engine}"

    )
    public void receive(final ConsumerRecord<String, byte[]> consumerRecord) {
        log.info("[KAFKA - Consumer] Received payload");

        try {
            final var message = AddClientWalkerProto.AddClientWalker.parseFrom(consumerRecord.value());
            payloads.add(message);

        } catch (Exception e) {
            log.error("[KAFKA - Consumer] Error parsing message. Error: {}", e.getMessage());
        }
    }

    public void emptyPayloads() {
        payloads.clear();
    }

}
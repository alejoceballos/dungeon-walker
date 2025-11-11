package momomomo.dungeonwalker.engine.transport.inbound;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.clientrequest.AddClientWalkerProto;
import momomomo.dungeonwalker.engine.domain.inbound.MessageReceiver;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumer {

    private final MessageReceiver<AddClientWalkerProto.AddClientWalker> eventListener;

    @KafkaListener(topics = "${kafka.topic.inbound.game-engine}")
    public void consume(final AddClientWalkerProto.AddClientWalker message) {
        log.info("---> [INBOUND - Kafka Consumer] Message received: {}", message);
        eventListener.onReceive(message);
    }

}

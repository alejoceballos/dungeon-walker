package momomomo.dungeonwalker.engine.transport.inbound;

import com.google.protobuf.InvalidProtocolBufferException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.clientrequest.AddClientWalkerProto.AddClientWalker;
import momomomo.dungeonwalker.engine.domain.inbound.MessageReceiver;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumer {

    private final MessageReceiver<AddClientWalker> eventListener;

    @KafkaListener(topics = "${kafka.topic.inbound.game-engine}")
    public void consume(final byte[] message) throws InvalidProtocolBufferException {
        log.info("---> [INBOUND - Kafka Consumer] Message received");
        final var proto = AddClientWalker.parseFrom(message);
        log.info("---> [INBOUND - Kafka Consumer] Message: \"{}\"", proto);
        eventListener.onReceive(proto);
    }

}

package momomomo.dungeonwalker.wsserver.transport.inbound;

import lombok.RequiredArgsConstructor;
import momomomo.dungeonwalker.contract.engine.EngineMessageProto.EngineMessage;
import momomomo.dungeonwalker.wsserver.domain.inbound.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PACKAGE;

@RequiredArgsConstructor(access = PACKAGE)
public class WsServerKafkaConsumer implements Consumer<EngineMessage> {

    private final KafkaConsumer<String, EngineMessage> consumer;
    private final String topic;

    @Override
    public void start() {
        consumer.subscribe(List.of(topic));
    }

    @Override
    public void stop() {
        consumer.unsubscribe();
    }

    @Override
    public List<EngineMessage> poll() {
        final var records = consumer.poll(Duration.ofMillis(100));
        final var messages = new ArrayList<EngineMessage>();

        records.iterator().forEachRemaining(consumerRecord -> messages.add(consumerRecord.value()));

        return messages;
    }

}

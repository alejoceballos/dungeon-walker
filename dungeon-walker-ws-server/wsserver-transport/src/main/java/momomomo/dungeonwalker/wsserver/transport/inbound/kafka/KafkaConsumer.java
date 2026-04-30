package momomomo.dungeonwalker.wsserver.transport.inbound.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.commons.conditional.Conditional;
import momomomo.dungeonwalker.contract.engine.EngineMessageProto.EngineMessage;
import momomomo.dungeonwalker.wsserver.domain.inbound.ConnectionManager;
import momomomo.dungeonwalker.wsserver.domain.input.engine.EngineMessageData;
import momomomo.dungeonwalker.wsserver.domain.input.engine.EngineMessageValue;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

import static java.util.Objects.nonNull;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumer {

    private static final String LABEL = "---> [CONSUMER - Kafka]";

    private final List<EngineMessageMapper<? extends EngineMessageValue>> mappers;
    private final ConnectionManager connectionManager;

    @KafkaListener(
            topics = "${kafka.topic.game-engine.inbound}",
            groupId = "${spring.kafka.consumer.group-id}",
            properties = {"${spring.kafka.consumer.auto-offset-reset}"}
    )
    public void consume(
            final ConsumerRecord<String, EngineMessage> consumerRecord,
            final Acknowledgment ack
    ) {
        log.info("{} Message received: \"{}\":\"{}\"", LABEL, consumerRecord.key(), consumerRecord.value());

        final var mappedValue = mappers
                .stream()
                .map(mapper -> mapper.map(consumerRecord))
                .filter(Objects::nonNull)
                .findAny()
                .orElse(null);

        Conditional
                .on(() -> nonNull(mappedValue))
                .thenExecute(() -> connectionManager.handleMessage(new EngineMessageData(mappedValue)))
                .alsoExecute(ack::acknowledge)
                .orElseExecute(() -> log.warn(
                        "{} Cannot map received message: \"{}\":\"{}\"",
                        LABEL,
                        consumerRecord.key(),
                        consumerRecord.value()))
                .evaluate();
    }

}

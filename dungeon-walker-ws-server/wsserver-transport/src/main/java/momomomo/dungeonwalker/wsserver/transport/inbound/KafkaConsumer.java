package momomomo.dungeonwalker.wsserver.transport.inbound;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.commons.conditional.Conditional;
import momomomo.dungeonwalker.contract.engine.EngineMessageProto.EngineMessage;
import momomomo.dungeonwalker.wsserver.domain.handler.HandlingResult;
import momomomo.dungeonwalker.wsserver.domain.inbound.EngineInbound;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.SPACE;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumer {

    private static final String LABEL = "---> [CONSUMER - Kafka]";

    private final EngineInbound<EngineMessage, CompletableFuture<HandlingResult>> inbound;

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

        inbound.handleMessage(consumerRecord.value())
                .whenComplete((result, ex) -> {
                    ack.acknowledge();
                    Conditional
                            .on(() -> isNull(ex))
                            .thenExecute(() -> log.debug(
                                    "{}{}. \"{}\":\"{}\"",
                                    LABEL,
                                    result.errors().isEmpty() ? EMPTY : SPACE + result.description(),
                                    consumerRecord.key(),
                                    consumerRecord.value()))
                            .orElseExecute(() -> log.error(
                                    "{} Failed to handle message: \"{}\":\"{}\". Error: {}",
                                    LABEL,
                                    consumerRecord.key(),
                                    consumerRecord.value(),
                                    ex.getMessage()));
                });
    }

}

package momomomo.dungeonwalker.wsserver.transport.inbound.kafka;

import momomomo.dungeonwalker.contract.engine.EngineMessageProto.EngineMessage;
import momomomo.dungeonwalker.wsserver.domain.input.engine.EngineMessageValue;
import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * Subclasses must transform incoming messages from the engine inbound kafka-topic to a subclass of
 * {@link EngineMessageValue} so it can be processed in another layer.
 * <p>
 * @see momomomo.dungeonwalker.wsserver.domain.input.engine.BroadcastMessage
 * </p>
 *
 * @param <O> a subclass of {@link EngineMessageValue} that the mapper will produce
 */
public interface EngineMessageMapper<O extends EngineMessageValue> {

    O map(ConsumerRecord<String, EngineMessage> consumerRecord);

}

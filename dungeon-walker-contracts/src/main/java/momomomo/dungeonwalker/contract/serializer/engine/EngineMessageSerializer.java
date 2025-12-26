package momomomo.dungeonwalker.contract.serializer.engine;

import momomomo.dungeonwalker.contract.engine.EngineMessageProto.EngineMessage;
import org.apache.kafka.common.serialization.Serializer;

public class EngineMessageSerializer implements Serializer<EngineMessage> {

    @Override
    public byte[] serialize(final String topic, final EngineMessage proto) {
        return proto.toByteArray();
    }

}

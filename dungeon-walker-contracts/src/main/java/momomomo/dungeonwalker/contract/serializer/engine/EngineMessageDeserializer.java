package momomomo.dungeonwalker.contract.serializer.engine;

import lombok.SneakyThrows;
import momomomo.dungeonwalker.contract.engine.EngineMessageProto.EngineMessage;
import org.apache.kafka.common.serialization.Deserializer;

public class EngineMessageDeserializer implements Deserializer<EngineMessage> {

    @SneakyThrows
    @Override
    public EngineMessage deserialize(final String topic, final byte[] bytes) {
        return EngineMessage.parseFrom(bytes);
    }

}

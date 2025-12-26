package momomomo.dungeonwalker.contract.serializer.client;

import lombok.SneakyThrows;
import momomomo.dungeonwalker.contract.client.ClientRequestProto.ClientRequest;
import org.apache.kafka.common.serialization.Deserializer;

public class ClientRequestDeserializer implements Deserializer<ClientRequest> {

    @SneakyThrows
    @Override
    public ClientRequest deserialize(final String topic, final byte[] bytes) {
        return ClientRequest.parseFrom(bytes);
    }

}

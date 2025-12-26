package momomomo.dungeonwalker.contract.serializer.client;

import momomomo.dungeonwalker.contract.client.ClientRequestProto.ClientRequest;
import org.apache.kafka.common.serialization.Serializer;

public class ClientRequestSerializer implements Serializer<ClientRequest> {

    @Override
    public byte[] serialize(final String topic, final ClientRequest proto) {
        return proto.toByteArray();
    }

}

package momomomo.dungeonwalker.contract.serializer;

import momomomo.dungeonwalker.contract.client.ClientRequestProto.ClientRequest;
import org.apache.kafka.common.serialization.Serializer;

public class ProtoSerializer implements Serializer<ClientRequest> {

    @Override
    public byte[] serialize(final String topic, final ClientRequest proto) {
        return proto.toByteArray();
    }

}

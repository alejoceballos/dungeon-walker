package momomomo.dungeonwalker.engine.transport.inbound.kafka;

import lombok.NonNull;
import momomomo.dungeonwalker.contract.client.ClientRequestProto.ClientRequest;
import momomomo.dungeonwalker.engine.domain.handler.MessageHandlerResult;

public class NoHandlerResult extends MessageHandlerResult<ClientRequest> {

    public NoHandlerResult(@NonNull ClientRequest message, @NonNull Throwable failure) {
        super(message, failure);
    }

}

package momomomo.dungeonwalker.engine.core.inbound;

import lombok.NonNull;
import momomomo.dungeonwalker.contract.client.ClientRequestProto.ClientRequest;
import momomomo.dungeonwalker.engine.domain.handler.MessageHandlerResult;

public class ClientRequestResult extends MessageHandlerResult<ClientRequest> {

    public ClientRequestResult(@NonNull ClientRequest message) {
        super(message);
    }

    public ClientRequestResult(@NonNull ClientRequest message, @NonNull Throwable failure) {
        super(message, failure);
    }

}

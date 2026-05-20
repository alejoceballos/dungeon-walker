package momomomo.dungeonwalker.wsserver.domain.inbound;

import lombok.NonNull;
import momomomo.dungeonwalker.wsserver.domain.data.client.input.Input;
import momomomo.dungeonwalker.wsserver.domain.outbound.ClientOutbound;

public interface ClientInbound {

    void establish(@NonNull ClientOutbound connection);

    void close(@NonNull String clientId);

    void handleMessage(@NonNull String clientId, @NonNull Input message);

}

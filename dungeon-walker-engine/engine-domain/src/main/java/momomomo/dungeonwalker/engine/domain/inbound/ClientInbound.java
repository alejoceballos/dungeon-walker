package momomomo.dungeonwalker.engine.domain.inbound;

import lombok.NonNull;

public interface ClientInbound<M> {

    void handleMessage(@NonNull M message);

}

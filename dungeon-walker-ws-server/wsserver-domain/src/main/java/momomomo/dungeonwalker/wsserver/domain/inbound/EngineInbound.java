package momomomo.dungeonwalker.wsserver.domain.inbound;

import lombok.NonNull;

public interface EngineInbound<M> {

    void handleMessage(@NonNull M message);

}

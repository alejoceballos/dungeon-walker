package momomomo.dungeonwalker.wsserver.domain.inbound;

import lombok.NonNull;

public interface EngineInbound<M, R> {

    R handleMessage(@NonNull M message);

}

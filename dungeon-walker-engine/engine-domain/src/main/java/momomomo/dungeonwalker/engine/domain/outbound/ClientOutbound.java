package momomomo.dungeonwalker.engine.domain.outbound;

import lombok.NonNull;

public interface ClientOutbound<M> {

    void send(@NonNull M message);

}

package momomomo.dungeonwalker.engine.domain.outbound;

import lombok.NonNull;

public interface Sender<M> {

    void send(@NonNull M message);

}

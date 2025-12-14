package momomomo.dungeonwalker.engine.domain.outbound;

import jakarta.annotation.Nonnull;

public interface Sender<M> {

    void send(@Nonnull M message);

}

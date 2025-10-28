package momomomo.dungeonwalker.wsserver.domain.outbound;

import jakarta.annotation.Nonnull;

public interface Sender<M> {

    void send(@Nonnull M message);

}

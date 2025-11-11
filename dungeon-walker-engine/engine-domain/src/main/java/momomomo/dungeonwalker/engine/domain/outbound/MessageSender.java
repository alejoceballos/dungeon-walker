package momomomo.dungeonwalker.engine.domain.outbound;

import jakarta.annotation.Nonnull;

public interface MessageSender<M> {

    void send(@Nonnull M message);

}

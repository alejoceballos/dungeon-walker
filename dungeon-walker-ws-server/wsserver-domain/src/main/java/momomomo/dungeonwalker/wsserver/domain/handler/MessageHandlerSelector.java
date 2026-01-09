package momomomo.dungeonwalker.wsserver.domain.handler;

import jakarta.annotation.Nonnull;

public interface MessageHandlerSelector<M, S, R> {

    @Nonnull MessageHandler<M, S, R> select(@Nonnull M message);

}

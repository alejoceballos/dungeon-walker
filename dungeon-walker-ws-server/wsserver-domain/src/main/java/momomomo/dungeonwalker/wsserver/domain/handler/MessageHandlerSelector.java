package momomomo.dungeonwalker.wsserver.domain.handler;

import jakarta.annotation.Nonnull;

public interface MessageHandlerSelector<M, R> {

    @Nonnull MessageHandler<M, R> select(@Nonnull M message);

}

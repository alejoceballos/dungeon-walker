package momomomo.dungeonwalker.engine.domain.handler;

import jakarta.annotation.Nonnull;

public interface MessageHandler<M> {

    MessageHandlerResult<M> handle(@Nonnull M message);

}

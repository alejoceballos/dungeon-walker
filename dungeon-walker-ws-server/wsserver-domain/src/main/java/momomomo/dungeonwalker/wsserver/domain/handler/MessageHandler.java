package momomomo.dungeonwalker.wsserver.domain.handler;

import jakarta.annotation.Nonnull;

public interface MessageHandler<M, S, R> {

    R handle(@Nonnull M message, @Nonnull S sender);
}

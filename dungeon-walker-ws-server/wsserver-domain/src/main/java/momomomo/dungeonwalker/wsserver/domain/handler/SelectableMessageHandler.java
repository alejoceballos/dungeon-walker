package momomomo.dungeonwalker.wsserver.domain.handler;

import jakarta.annotation.Nonnull;

public interface SelectableMessageHandler<M, S, R> extends MessageHandler<M, S, R> {

    boolean canHandle(@Nonnull M message);

}

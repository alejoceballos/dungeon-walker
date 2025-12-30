package momomomo.dungeonwalker.wsserver.domain.handler;

import jakarta.annotation.Nonnull;

public interface SelectableMessageHandler<M, R> extends MessageHandler<M, R> {

    boolean canHandle(@Nonnull M message);

}

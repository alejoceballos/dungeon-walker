package momomomo.dungeonwalker.engine.domain.handler;

import jakarta.annotation.Nonnull;

public interface SelectableHandler<M> extends MessageHandler<M> {

    boolean shouldHandle(@Nonnull M message);

}

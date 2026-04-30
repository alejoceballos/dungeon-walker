package momomomo.dungeonwalker.engine.domain.handler;

import lombok.NonNull;

public interface SelectableHandler<M> extends MessageHandler<M> {

    boolean shouldHandle(@NonNull M message);

}

package momomomo.dungeonwalker.engine.domain.handler;

import lombok.NonNull;

public interface MessageHandler<M> {

    MessageHandlerResult<M> handle(@NonNull M message);

}

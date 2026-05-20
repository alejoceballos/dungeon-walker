package momomomo.dungeonwalker.wsserver.domain.handler;

import lombok.NonNull;

public interface MessageHandlerSelector<M, R> {

    @NonNull
    MessageHandler<M, R> select(@NonNull M message);

}

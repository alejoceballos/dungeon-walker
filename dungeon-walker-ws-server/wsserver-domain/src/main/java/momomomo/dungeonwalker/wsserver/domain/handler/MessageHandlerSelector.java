package momomomo.dungeonwalker.wsserver.domain.handler;

import lombok.NonNull;

public interface MessageHandlerSelector<M, S, R> {

    @NonNull
    MessageHandler<M, S, R> select(@NonNull M message);

}

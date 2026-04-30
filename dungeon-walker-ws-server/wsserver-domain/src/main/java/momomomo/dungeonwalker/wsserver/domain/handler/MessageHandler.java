package momomomo.dungeonwalker.wsserver.domain.handler;

import lombok.NonNull;

public interface MessageHandler<M, S, R> {

    R handle(@NonNull M message, @NonNull S sender);

}

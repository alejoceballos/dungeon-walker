package momomomo.dungeonwalker.wsserver.domain.handler;

import lombok.NonNull;

public interface MessageHandler<M, R> {

    R handle(@NonNull M message);

}

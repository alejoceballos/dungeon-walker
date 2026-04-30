package momomomo.dungeonwalker.wsserver.domain.handler;

import lombok.NonNull;

public interface SelectableMessageHandler<M, S, R> extends MessageHandler<M, S, R> {

    boolean canHandle(@NonNull M message);

}

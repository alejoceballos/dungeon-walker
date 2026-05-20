package momomomo.dungeonwalker.wsserver.domain.handler;

import lombok.NonNull;

public interface SelectableMessageHandler<M, R> extends MessageHandler<M, R> {

    boolean canHandle(@NonNull M message);

}

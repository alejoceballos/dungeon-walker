package momomomo.dungeonwalker.wsserver.domain.handler;

import jakarta.annotation.Nonnull;
import momomomo.dungeonwalker.wsserver.domain.inbound.ClientConnection;

public interface MessageHandler<M, R> {

    R handle(@Nonnull M message, @Nonnull ClientConnection connection);
}

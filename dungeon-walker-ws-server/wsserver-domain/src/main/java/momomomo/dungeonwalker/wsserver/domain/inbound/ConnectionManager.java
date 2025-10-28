package momomomo.dungeonwalker.wsserver.domain.inbound;

import jakarta.annotation.Nonnull;
import momomomo.dungeonwalker.wsserver.domain.input.Input;

public interface ConnectionManager {

    void establish(@Nonnull ClientConnection connection);

    void close(@Nonnull ClientConnection connection);

    void handleMessage(@Nonnull ClientConnection connection, @Nonnull Input message);

}

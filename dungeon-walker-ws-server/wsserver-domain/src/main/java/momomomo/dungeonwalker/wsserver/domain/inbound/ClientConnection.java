package momomomo.dungeonwalker.wsserver.domain.inbound;

import jakarta.annotation.Nonnull;
import momomomo.dungeonwalker.wsserver.domain.output.Output;

public interface ClientConnection {

    @Nonnull
    String getSessionId();

    @Nonnull
    String getUserId();

    void close();

    void send(@Nonnull Output message);

}

package momomomo.dungeonwalker.wsserver.domain.inbound;

import lombok.NonNull;
import momomomo.dungeonwalker.wsserver.domain.output.Output;

public interface ClientConnection {

    @NonNull
    String getSessionId();

    @NonNull
    String getUserId();

    void close();

    boolean isOpen();

    boolean isClosed();

    void send(@NonNull Output message);

}

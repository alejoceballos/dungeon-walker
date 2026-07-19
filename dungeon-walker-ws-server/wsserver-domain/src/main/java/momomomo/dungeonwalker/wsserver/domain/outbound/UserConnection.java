package momomomo.dungeonwalker.wsserver.domain.outbound;

import lombok.NonNull;
import momomomo.dungeonwalker.wsserver.domain.data.user.output.Output;

public interface UserConnection {

    @NonNull
    String getId();

    boolean isConnected();

    void disconnect();

    void send(@NonNull Output message);

    void sendAsync(@NonNull Output message);

}

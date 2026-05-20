package momomomo.dungeonwalker.wsserver.domain.outbound;

import lombok.NonNull;
import momomomo.dungeonwalker.wsserver.domain.data.client.output.Output;

public interface ClientOutbound {

    @NonNull
    String getId();

    void close();

    void send(@NonNull Output message);

}

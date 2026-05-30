package momomomo.dungeonwalker.wsserver.domain.inbound;

import lombok.NonNull;
import momomomo.dungeonwalker.wsserver.domain.data.user.input.Input;
import momomomo.dungeonwalker.wsserver.domain.outbound.UserConnection;

public interface UserInbound {

    void establish(@NonNull UserConnection connection);

    void close(@NonNull final UserConnection userConnection);

    void handle(@NonNull final UserConnection userConnection, @NonNull Input message);

}

package momomomo.dungeonwalker.wsserver.domain.inbound;

import lombok.NonNull;
import momomomo.dungeonwalker.wsserver.domain.input.client.Input;
import momomomo.dungeonwalker.wsserver.domain.input.engine.EngineMessageData;

public interface ConnectionManager {

    void establish(@NonNull ClientConnection connection);

    void close(@NonNull ClientConnection connection);

    void handleMessage(@NonNull ClientConnection connection, @NonNull Input message);

    void handleMessage(@NonNull EngineMessageData message);

}

package momomomo.dungeonwalker.wsserver.domain.connection;

import momomomo.dungeonwalker.wsserver.domain.input.Input;

public interface ConnectionManager {

    void establish(ClientConnection connection);

    void close(ClientConnection connection);

    void handleMessage(final ClientConnection connection, Input message);

}

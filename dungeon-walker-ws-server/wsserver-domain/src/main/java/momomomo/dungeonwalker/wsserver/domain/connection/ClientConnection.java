package momomomo.dungeonwalker.wsserver.domain.connection;

import momomomo.dungeonwalker.wsserver.domain.output.Output;

public interface ClientConnection {

    String getSessionId();

    String getUserId();

    void close();

    void send(Output message);

}

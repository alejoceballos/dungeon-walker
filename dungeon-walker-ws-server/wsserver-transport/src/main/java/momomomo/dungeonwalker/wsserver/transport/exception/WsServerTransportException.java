package momomomo.dungeonwalker.wsserver.transport.exception;

import lombok.NonNull;

public class WsServerTransportException extends RuntimeException {

    public WsServerTransportException(@NonNull final Throwable cause) {
        super(cause);
    }

}

package momomomo.dungeonwalker.wsserver.domain.auth;

public class WsServerExpiredAuthorizationException extends RuntimeException {

    public WsServerExpiredAuthorizationException(final String message) {
        super(message);
    }

    public WsServerExpiredAuthorizationException(final Throwable cause) {
        super(cause);
    }

}

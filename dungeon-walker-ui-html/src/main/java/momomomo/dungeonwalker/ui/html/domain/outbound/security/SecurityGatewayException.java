package momomomo.dungeonwalker.ui.html.domain.outbound.security;

import lombok.Getter;

public class SecurityGatewayException extends RuntimeException {

    public enum Type {
        CREDENTIALS_FATAL_ERROR,
        CREDENTIALS_CLIENT_ERROR,
        CREDENTIALS_SERVER_ERROR,
        CREDENTIALS_UNMAPPED_ERROR,
        CONNECTION_FATAL_ERROR,
        CONNECTION_ERROR
    }

    @Getter
    private final Type type;

    public SecurityGatewayException(final String message, final Type type) {
        super(message);
        this.type = type;
    }

    public SecurityGatewayException(final Throwable cause, final Type type) {
        super(cause);
        this.type = type;
    }

}

package momomomo.dungeonwalker.engine.domain;

public class DomainException extends RuntimeException {

    public DomainException(final String message) {
        super(message);
    }

    public DomainException(final Throwable cause) {
        super(cause);
    }

}

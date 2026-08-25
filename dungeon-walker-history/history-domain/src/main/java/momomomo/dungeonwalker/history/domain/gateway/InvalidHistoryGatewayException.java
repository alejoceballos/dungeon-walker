package momomomo.dungeonwalker.history.domain.gateway;

public class InvalidHistoryGatewayException extends RuntimeException {

    public InvalidHistoryGatewayException(final String message) {
        super(message);
    }

}

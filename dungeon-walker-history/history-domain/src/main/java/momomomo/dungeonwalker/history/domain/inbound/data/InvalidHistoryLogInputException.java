package momomomo.dungeonwalker.history.domain.inbound.data;

public class InvalidHistoryLogInputException extends RuntimeException{

    public InvalidHistoryLogInputException(final String message) {
        super(message);
    }

}

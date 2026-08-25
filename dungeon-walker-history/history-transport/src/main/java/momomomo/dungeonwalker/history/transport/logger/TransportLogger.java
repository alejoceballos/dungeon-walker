package momomomo.dungeonwalker.history.transport.logger;

import static momomomo.dungeonwalker.history.domain.context.CorrelationId.CORRELATION_ID;
import static org.apache.commons.lang3.StringUtils.EMPTY;

public interface TransportLogger {

    default String logMsg(final String label, final String message) {
        final var correlationIdMessage = CORRELATION_ID.isBound()
                ? "CorrelationId: %s".formatted(CORRELATION_ID.get())
                : EMPTY;
        return "--> [TRANSPORT][%s] %s [%s]".formatted(label, message, correlationIdMessage);
    }

}

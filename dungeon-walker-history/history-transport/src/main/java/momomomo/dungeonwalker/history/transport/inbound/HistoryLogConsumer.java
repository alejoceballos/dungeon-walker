package momomomo.dungeonwalker.history.transport.inbound;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.contract.history.HistoryLog;
import momomomo.dungeonwalker.contract.history.LogCoordinates;
import momomomo.dungeonwalker.contract.history.walker.EnterLog;
import momomomo.dungeonwalker.contract.history.walker.LeaveLog;
import momomomo.dungeonwalker.contract.history.walker.MoveLog;
import momomomo.dungeonwalker.history.domain.inbound.HistoryLogPort;
import momomomo.dungeonwalker.history.domain.inbound.data.CoordinatesLogInput;
import momomomo.dungeonwalker.history.domain.inbound.data.HistoryLogInput;
import momomomo.dungeonwalker.history.domain.inbound.data.WalkerLogInputFactory;
import momomomo.dungeonwalker.history.transport.logger.TransportLogger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;
import java.util.function.Consumer;

import static momomomo.dungeonwalker.history.domain.context.CorrelationId.CORRELATION_ID;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class HistoryLogConsumer implements TransportLogger {

    private static final String LABEL = HistoryLogConsumer.class.getSimpleName();

    private final WalkerLogInputFactory walkerLogInputFactory;
    private final HistoryLogPort historyLogPort;

    @Bean
    public Consumer<HistoryLog> consumeHistoryLog() {
        return historyLog ->
                ScopedValue.where(CORRELATION_ID, UUID.randomUUID()).run(() -> {
                    log.info(logMsg(LABEL, "[IN] Received message: {}"), historyLog);

                    final var historyLogInput = map(historyLog);

                    log.debug(logMsg(LABEL, "Message mapped to: {}"), historyLogInput);

                    historyLogPort.create(historyLogInput);
                });
    }

    private HistoryLogInput map(final HistoryLog historyLog) {
        return switch (historyLog) {
            case final EnterLog walkerLog -> walkerLogInputFactory
                    .createEnterLog(
                            walkerLog.getTimestamp(),
                            walkerLog.getWalkerId(),
                            map(walkerLog.getStartCoordinates()));
            case final MoveLog walkerLog -> walkerLogInputFactory
                    .createMoveLog(
                            walkerLog.getTimestamp(),
                            walkerLog.getWalkerId(),
                            map(walkerLog.getFrom()),
                            map(walkerLog.getTo()));
            case final LeaveLog walkerLog -> walkerLogInputFactory
                    .createLeaveLog(
                            walkerLog.getTimestamp(),
                            walkerLog.getWalkerId(),
                            walkerLog.getReason());
            default -> throw new IllegalArgumentException(
                    "Unsupported HistoryLog type: " + historyLog.getClass().getName());
        };
    }

    private CoordinatesLogInput map(final LogCoordinates coordinates) {
        return new CoordinatesLogInput(coordinates.x(), coordinates.y());
    }

}

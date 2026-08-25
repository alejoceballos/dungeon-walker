package momomomo.dungeonwalker.engine.transport.outbound;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.contract.history.HistoryLog;
import momomomo.dungeonwalker.contract.history.LogCoordinates;
import momomomo.dungeonwalker.contract.history.walker.EnterLog;
import momomomo.dungeonwalker.contract.history.walker.LeaveLog;
import momomomo.dungeonwalker.engine.domain.outbound.ClientOutbound;
import momomomo.dungeonwalker.engine.domain.outbound.WalkerHistoryLog;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitMqProducer implements ClientOutbound<WalkerHistoryLog> {

    private static final String LABEL = "---> [OUTBOUND - RabbitMQ Producer]";

    private final StreamBridge streamBridge;

    @Override
    public void send(final @NonNull WalkerHistoryLog historyLog) {
        log.debug("---> {} Sending history log \"{}\".", LABEL, historyLog);

        final var result = streamBridge.send("consumeHistoryLog-out-0", mapToContract(historyLog));

        log.debug("---> {} History log \"{}\" {} sent.", LABEL, historyLog, result ? "successfully" : "NOT");
    }

    private static HistoryLog mapToContract(final @NonNull WalkerHistoryLog walkerHistoryLog) {
        return switch (walkerHistoryLog.type()) {
            case ENTER -> convertToEnterHistoryLog(walkerHistoryLog);
            case LEAVE -> convertToLeaveHistoryLog(walkerHistoryLog);
            case MOVE -> convertToMoveHistoryLog(walkerHistoryLog);
        };
    }

    @SuppressWarnings("ConstantConditions")
    private static HistoryLog convertToEnterHistoryLog(@NonNull final WalkerHistoryLog walkerHistoryLog) {
        return new EnterLog(
                walkerHistoryLog.timestamp(),
                walkerHistoryLog.walkerId(),
                new LogCoordinates(
                        walkerHistoryLog.to().getX(),
                        walkerHistoryLog.to().getY())

        );
    }

    @SuppressWarnings("ConstantConditions")
    private static HistoryLog convertToLeaveHistoryLog(@NonNull final WalkerHistoryLog walkerHistoryLog) {
        return new LeaveLog(
                walkerHistoryLog.timestamp(),
                walkerHistoryLog.walkerId(),
                walkerHistoryLog.notes()
        );
    }

    @SuppressWarnings("ConstantConditions")
    private static HistoryLog convertToMoveHistoryLog(@NonNull final WalkerHistoryLog walkerHistoryLog) {
        return new momomomo.dungeonwalker.contract.history.walker.MoveLog(
                walkerHistoryLog.timestamp(),
                walkerHistoryLog.walkerId(),
                new LogCoordinates(
                        walkerHistoryLog.from().getX(),
                        walkerHistoryLog.from().getY()),
                new LogCoordinates(
                        walkerHistoryLog.to().getX(),
                        walkerHistoryLog.to().getY())
        );
    }

}

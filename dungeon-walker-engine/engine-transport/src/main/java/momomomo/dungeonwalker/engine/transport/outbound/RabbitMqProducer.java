package momomomo.dungeonwalker.engine.transport.outbound;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.engine.domain.outbound.ClientOutbound;
import momomomo.dungeonwalker.engine.domain.outbound.HistoryLog;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitMqProducer implements ClientOutbound<HistoryLog> {

    private static final String LABEL = "---> [OUTBOUND - RabbitMQ Producer]";

    private final StreamBridge streamBridge;

    @Override
    public void send(final @NonNull HistoryLog historyLog) {
        log.debug("---> {} Sending history log \"{}\".", LABEL, historyLog);

        final var result = streamBridge.send("consumeHistoryLog-out-0", historyLog);

        log.debug("---> {} History log \"{}\" {} sent.", LABEL, historyLog, result ? "successfully" : "NOT");
    }

}

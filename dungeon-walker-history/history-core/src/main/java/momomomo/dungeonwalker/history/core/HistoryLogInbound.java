package momomomo.dungeonwalker.history.core;

import momomomo.dungeonwalker.history.core.logger.CoreLogger;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.history.domain.gateway.HistoryGateway;
import momomomo.dungeonwalker.history.domain.inbound.HistoryLogPort;
import momomomo.dungeonwalker.history.domain.inbound.data.HistoryLogInput;
import momomomo.dungeonwalker.history.domain.model.History;
import momomomo.dungeonwalker.history.domain.model.HistoryFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class HistoryLogInbound implements HistoryLogPort, CoreLogger {

    private static final String LABEL = HistoryLogInbound.class.getSimpleName();

    private final HistoryFactory historyFactory;
    private final HistoryGateway historyGateway;

    @Override
    public void create(@NonNull final HistoryLogInput historyLogInput) {
        log.debug(logMsg(LABEL, "Will create a history log for {}"), historyLogInput);

        final var savedHistory = historyGateway.save(toDomain(historyLogInput));

        log.info(logMsg(LABEL, "History created: {}"), savedHistory);
    }

    private @NonNull History toDomain(@NonNull final HistoryLogInput historyLogInput) {
        return Optional
                .of(historyLogInput)
                .map(historyFactory::create)
                .orElseThrow(() -> new IllegalArgumentException("HistoryLogInput cannot be null"));
    }

}

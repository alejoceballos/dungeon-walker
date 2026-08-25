package momomomo.dungeonwalker.history.transport.gateway;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.history.domain.gateway.HistoryGateway;
import momomomo.dungeonwalker.history.domain.gateway.InvalidHistoryGatewayException;
import momomomo.dungeonwalker.history.domain.model.History;
import momomomo.dungeonwalker.history.transport.logger.TransportLogger;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Supplier;

@Slf4j
@Component
@RequiredArgsConstructor
public class HistoryMapperGateway implements HistoryGateway, TransportLogger {

    private static final String LABEL = HistoryMapperGateway.class.getSimpleName();

    private final List<SelectableGateway> selectableRepositories;

    @SuppressWarnings("unchecked")
    @Override
    public History save(@NonNull final History history) {
        log.debug(logMsg(LABEL, "Will save history: {}"), history);

        final var savedHistory = selectableRepositories
                .stream()
                .filter(repository -> repository.isResponsibleFor(history) && repository instanceof SaveGateway)
                .findAny()
                .map(SaveGateway.class::cast)
                .orElseThrow(noSaveRepositoryFound(history))
                .save(history);

        log.info(logMsg(LABEL, "History saved: {}"), savedHistory);

        return (History) savedHistory;
    }

    private static @NonNull Supplier<InvalidHistoryGatewayException> noSaveRepositoryFound(@NonNull final History history) {
        return () -> new InvalidHistoryGatewayException("No repository found to save " + history.getClass().getSimpleName());
    }

}

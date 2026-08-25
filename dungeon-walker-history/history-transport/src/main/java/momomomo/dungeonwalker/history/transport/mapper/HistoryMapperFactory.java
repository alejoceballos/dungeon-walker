package momomomo.dungeonwalker.history.transport.mapper;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.history.domain.inbound.data.HistoryLogInput;
import momomomo.dungeonwalker.history.domain.inbound.data.InvalidHistoryLogInputException;
import momomomo.dungeonwalker.history.domain.model.History;
import momomomo.dungeonwalker.history.domain.model.HistoryFactory;
import momomomo.dungeonwalker.history.transport.inbound.data.WalkerLogInputData;
import momomomo.dungeonwalker.history.transport.logger.TransportLogger;
import momomomo.dungeonwalker.history.transport.mapper.map.CoordinatesMapper;
import momomomo.dungeonwalker.history.transport.mapper.walker.WalkerHistoryMapperFactory;
import momomomo.dungeonwalker.history.transport.mapper.walker.WalkerMapperFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class HistoryMapperFactory implements HistoryFactory, TransportLogger {

    private static final String LABEL = HistoryMapperFactory.class.getSimpleName();

    private final WalkerMapperFactory walkerMapperFactory;
    private final WalkerHistoryMapperFactory walkerHistoryMapperFactory;

    @Override
    public @NonNull History create(@NonNull final HistoryLogInput input) {
        log.info(logMsg(LABEL, "Creating history from: {}"), input);

        if (input instanceof final WalkerLogInputData data) {
            return walkerMapperFactory.create(
                    data.getWalkerId(),
                    List.of(Optional
                            .ofNullable(data.getTo())
                            .map(to -> walkerHistoryMapperFactory.create(
                                    data.getTimestamp(),
                                    new CoordinatesMapper(to.x(), to.y())))
                            .orElse(walkerHistoryMapperFactory.create(data.getTimestamp()))));

        }

        throw new InvalidHistoryLogInputException("Invalid history log input type: " + input.getClass().getName());
    }

}

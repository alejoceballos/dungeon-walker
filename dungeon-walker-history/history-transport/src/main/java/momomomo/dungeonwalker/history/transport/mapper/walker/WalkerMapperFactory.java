package momomomo.dungeonwalker.history.transport.mapper.walker;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import momomomo.dungeonwalker.history.domain.model.walker.Walker;
import momomomo.dungeonwalker.history.domain.model.walker.WalkerFactory;
import momomomo.dungeonwalker.history.domain.model.walker.WalkerHistory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class WalkerMapperFactory implements WalkerFactory {

    private final WalkerHistoryMapperFactory walkerHistoryMapperFactory;

    @Override
    public @NonNull Walker create(@NonNull final String systemId) {
        return new WalkerMapper(null, systemId, List.of());
    }

    @Override
    public @NonNull Walker create(@NonNull final String systemId, @NonNull final List<WalkerHistory> history) {
        return new WalkerMapper(null, systemId, mapHistory(history));
    }

    @Override
    public @NonNull Walker create(
            @NonNull final Long id,
            @NonNull final String systemId,
            @NonNull final List<WalkerHistory> history
    ) {
        return new WalkerMapper(id, systemId, mapHistory(history));
    }

    private static @NonNull List<WalkerHistoryMapper> mapHistory(@NonNull final List<WalkerHistory> history) {
        return history
                .stream()
                .map(WalkerHistoryMapper.class::cast)
                .toList();
    }

}

package momomomo.dungeonwalker.history.transport.mapper.walker;

import lombok.NonNull;
import momomomo.dungeonwalker.history.domain.model.map.Coordinates;
import momomomo.dungeonwalker.history.domain.model.walker.WalkerHistory;
import momomomo.dungeonwalker.history.domain.model.walker.WalkerHistoryFactory;
import momomomo.dungeonwalker.history.transport.mapper.map.CoordinatesMapper;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class WalkerHistoryMapperFactory implements WalkerHistoryFactory {

    @Override
    public @NonNull WalkerHistory create(@NonNull final Instant timestamp) {
        return WalkerHistoryMapper
                .builder()
                .timestamp(timestamp.atOffset(java.time.ZoneOffset.UTC))
                .build();
    }

    @Override
    public @NonNull WalkerHistory create(@NonNull final Instant timestamp, @NonNull final Coordinates coordinates) {
        return WalkerHistoryMapper
                .builder()
                .timestamp(timestamp.atOffset(java.time.ZoneOffset.UTC))
                .coordinates(new CoordinatesMapper(coordinates.getX(), coordinates.getY()))
                .build();
    }

    @Override
    public @NonNull WalkerHistory create(
            @NonNull final Long id,
            @NonNull final Instant timestamp,
            @NonNull final Coordinates coordinates
    ) {
        return WalkerHistoryMapper
                .builder()
                .id(id)
                .timestamp(timestamp.atOffset(java.time.ZoneOffset.UTC))
                .coordinates(new CoordinatesMapper(coordinates.getX(), coordinates.getY()))
                .build();
    }

}

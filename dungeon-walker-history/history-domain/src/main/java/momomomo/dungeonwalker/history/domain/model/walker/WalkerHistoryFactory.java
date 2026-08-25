package momomomo.dungeonwalker.history.domain.model.walker;

import lombok.NonNull;
import momomomo.dungeonwalker.history.domain.model.map.Coordinates;

import java.time.Instant;

public interface WalkerHistoryFactory {

    @NonNull
    WalkerHistory create(@NonNull Instant timestamp);

    @NonNull
    WalkerHistory create(@NonNull Instant timestamp, @NonNull Coordinates coordinates);

    @NonNull
    WalkerHistory create(@NonNull Long id, @NonNull Instant timestamp, @NonNull Coordinates coordinates);

}

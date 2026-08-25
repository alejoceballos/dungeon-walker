package momomomo.dungeonwalker.history.domain.model.walker;

import lombok.NonNull;

import java.util.List;

public interface WalkerFactory {

    @NonNull
    Walker create(@NonNull String systemId);

    @NonNull
    Walker create(@NonNull String systemId, @NonNull List<WalkerHistory> history);

    @NonNull
    Walker create(@NonNull Long id, @NonNull String systemId, @NonNull List<WalkerHistory> history);

}

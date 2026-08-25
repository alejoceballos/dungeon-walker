package momomomo.dungeonwalker.history.domain.model;

import lombok.NonNull;
import momomomo.dungeonwalker.history.domain.inbound.data.HistoryLogInput;

public interface HistoryFactory {

    @NonNull
    History create(@NonNull HistoryLogInput input);

}

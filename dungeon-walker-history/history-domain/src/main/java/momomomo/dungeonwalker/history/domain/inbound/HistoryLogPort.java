package momomomo.dungeonwalker.history.domain.inbound;

import lombok.NonNull;
import momomomo.dungeonwalker.history.domain.inbound.data.HistoryLogInput;

public interface HistoryLogPort {

    void create(@NonNull HistoryLogInput historyLog);

}

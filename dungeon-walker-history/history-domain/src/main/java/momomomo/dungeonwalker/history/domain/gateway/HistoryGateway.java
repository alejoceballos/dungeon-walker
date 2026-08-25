package momomomo.dungeonwalker.history.domain.gateway;

import lombok.NonNull;
import momomomo.dungeonwalker.history.domain.model.History;

public interface HistoryGateway {

    History save(@NonNull History history);

}

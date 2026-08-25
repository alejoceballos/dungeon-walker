package momomomo.dungeonwalker.history.transport.gateway;

import lombok.NonNull;
import momomomo.dungeonwalker.history.domain.model.History;

public interface SelectableGateway {

    boolean isResponsibleFor(@NonNull History history);
}

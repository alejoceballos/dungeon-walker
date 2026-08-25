package momomomo.dungeonwalker.history.domain.model.walker;

import momomomo.dungeonwalker.history.domain.model.History;

import java.util.List;

public interface Walker extends History {

    String getSystemId();

    List<WalkerHistory> getHistory();

}

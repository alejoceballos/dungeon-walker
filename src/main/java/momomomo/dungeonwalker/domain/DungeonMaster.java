package momomomo.dungeonwalker.domain;

import momomomo.dungeonwalker.domain.event.DungeonEvent;
import momomomo.dungeonwalker.domain.event.DungeonEventListener;

public interface DungeonMaster {

    void addEventListener(DungeonEventListener<? extends DungeonEvent> eventListener);
    void addWalker(String id);

}

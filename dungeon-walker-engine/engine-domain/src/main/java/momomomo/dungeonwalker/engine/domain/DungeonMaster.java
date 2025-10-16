package momomomo.dungeonwalker.engine.domain;

import momomomo.dungeonwalker.engine.domain.event.DungeonEvent;
import momomomo.dungeonwalker.engine.domain.event.DungeonEventListener;

public interface DungeonMaster {

    void addEventListener(DungeonEventListener<? extends DungeonEvent> eventListener);
    void addWalker(String id);

}

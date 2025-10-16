package momomomo.dungeonwalker.engine.domain.event;

public interface DungeonEventListener<T extends DungeonEvent> {

    void onEvent(T event);

}

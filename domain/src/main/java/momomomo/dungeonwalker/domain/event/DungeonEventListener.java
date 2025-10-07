package momomomo.dungeonwalker.domain.event;

public interface DungeonEventListener<T extends DungeonEvent> {

    void onEvent(T event);

}

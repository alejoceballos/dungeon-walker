package momomomo.dungeonwalker.engine.core.actor.dungeon.command;

import momomomo.dungeonwalker.engine.domain.event.DungeonEvent;
import momomomo.dungeonwalker.engine.domain.event.DungeonEventListener;

public record AddSpectator(DungeonEventListener<? extends DungeonEvent> eventListener) {
}

package momomomo.dungeonwalker.engine.guardian.command;

import momomomo.dungeonwalker.domain.event.DungeonEventListener;

@SuppressWarnings("rawtypes")
public record AddEventListener(DungeonEventListener eventListener) implements EngineCommand {
}

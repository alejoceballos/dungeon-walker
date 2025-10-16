package momomomo.dungeonwalker.engine.core.guardian.command;

import momomomo.dungeonwalker.engine.domain.event.DungeonEventListener;

@SuppressWarnings("rawtypes")
public record AddEventListener(DungeonEventListener eventListener) implements EngineCommand {
}

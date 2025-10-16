package momomomo.dungeonwalker.engine.core.guardian.command;

import momomomo.dungeonwalker.engine.domain.model.dungeon.Dungeon;

public record DungeonReady(Dungeon dungeon) implements EngineCommand {
}

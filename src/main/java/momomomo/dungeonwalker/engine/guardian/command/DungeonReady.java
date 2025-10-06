package momomomo.dungeonwalker.engine.guardian.command;

import momomomo.dungeonwalker.domain.model.dungeon.Dungeon;

public record DungeonReady(Dungeon dungeon) implements EngineCommand {
}

package momomomo.dungeonwalker.engine.core.actor.walker.command.from.client;

import lombok.NonNull;
import momomomo.dungeonwalker.engine.core.actor.walker.command.WalkerCommand;
import momomomo.dungeonwalker.engine.domain.model.dungeon.placing.DungeonPlacingStrategy;

public record WakeUp(@NonNull DungeonPlacingStrategy placingStrategy) implements WalkerCommand {
}

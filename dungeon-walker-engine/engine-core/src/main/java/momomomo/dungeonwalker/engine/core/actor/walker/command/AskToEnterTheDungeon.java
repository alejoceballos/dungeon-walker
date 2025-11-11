package momomomo.dungeonwalker.engine.core.actor.walker.command;

import akka.cluster.sharding.typed.javadsl.EntityRef;
import lombok.NonNull;
import momomomo.dungeonwalker.engine.core.actor.dungeon.command.DungeonCommand;
import momomomo.dungeonwalker.engine.domain.model.dungeon.placing.DungeonPlacingStrategy;
import momomomo.dungeonwalker.engine.domain.model.walker.moving.WalkerMovingStrategy;

public record AskToEnterTheDungeon(
        @NonNull EntityRef<DungeonCommand> dungeonRef,
        @NonNull DungeonPlacingStrategy placingStrategy,
        @NonNull WalkerMovingStrategy movingStrategy
) implements WalkerCommand {
}

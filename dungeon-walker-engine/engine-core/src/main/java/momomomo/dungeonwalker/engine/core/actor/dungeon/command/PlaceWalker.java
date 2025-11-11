package momomomo.dungeonwalker.engine.core.actor.dungeon.command;

import akka.actor.typed.ActorRef;
import lombok.NonNull;
import momomomo.dungeonwalker.engine.core.actor.walker.command.WalkerCommand;
import momomomo.dungeonwalker.engine.domain.model.dungeon.placing.DungeonPlacingStrategy;
import momomomo.dungeonwalker.engine.domain.model.walker.Walker;

public record PlaceWalker(
        @NonNull ActorRef<WalkerCommand> walkerRef,
        @NonNull Walker walker,
        @NonNull DungeonPlacingStrategy placingStrategy
) implements DungeonCommand {
}

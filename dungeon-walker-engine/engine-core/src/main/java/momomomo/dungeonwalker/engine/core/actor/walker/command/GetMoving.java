package momomomo.dungeonwalker.engine.core.actor.walker.command;

import lombok.NonNull;

public record GetMoving(@NonNull String dungeonEntityId) implements WalkerCommand {
}

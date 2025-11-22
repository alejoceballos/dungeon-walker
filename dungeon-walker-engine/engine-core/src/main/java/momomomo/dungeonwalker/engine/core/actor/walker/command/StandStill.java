package momomomo.dungeonwalker.engine.core.actor.walker.command;

import lombok.NonNull;

public record StandStill(@NonNull String dungeonEntityId) implements WalkerCommand {
}

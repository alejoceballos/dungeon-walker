package momomomo.dungeonwalker.engine.core.actor.walker.command;

import lombok.NonNull;
import momomomo.dungeonwalker.engine.domain.model.walker.state.WalkerState;

public record WalkerStateReply(@NonNull Class<? extends WalkerState> value) implements WalkerCommand {
}

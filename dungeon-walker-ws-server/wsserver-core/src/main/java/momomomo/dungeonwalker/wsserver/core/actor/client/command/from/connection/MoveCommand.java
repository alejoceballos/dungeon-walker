package momomomo.dungeonwalker.wsserver.core.actor.client.command.from.connection;

import lombok.NonNull;
import momomomo.dungeonwalker.wsserver.core.actor.client.command.ClientCommand;
import momomomo.dungeonwalker.wsserver.domain.data.user.input.Direction;

public record MoveCommand(@NonNull Direction direction) implements ClientCommand {
}

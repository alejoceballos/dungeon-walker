package momomomo.dungeonwalker.wsserver.core.actor.client.command.from.connection;

import jakarta.annotation.Nullable;
import momomomo.dungeonwalker.wsserver.core.actor.client.command.ClientCommand;

public record ConnectionCloseCommand(@Nullable String reason) implements ClientCommand {
}

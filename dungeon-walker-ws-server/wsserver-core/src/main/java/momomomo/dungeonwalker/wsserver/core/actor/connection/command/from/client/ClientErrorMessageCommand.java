package momomomo.dungeonwalker.wsserver.core.actor.connection.command.from.client;

import jakarta.annotation.Nullable;
import lombok.NonNull;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.ConnectionCommand;

public record ClientErrorMessageCommand(@NonNull String error, @Nullable Throwable exception) implements ConnectionCommand {
}

package momomomo.dungeonwalker.wsserver.core.actor.connection.command.from.client;

import lombok.NonNull;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.ConnectionCommand;

import java.time.Instant;

public record HeartbeatFromClient(@NonNull Instant timestamp) implements ConnectionCommand {
}

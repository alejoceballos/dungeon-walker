package momomomo.dungeonwalker.wsserver.core.connection.command;

import momomomo.dungeonwalker.wsserver.domain.input.Input;

public record SendMessageFromClient(Input message) implements ConnectionCommand {
}

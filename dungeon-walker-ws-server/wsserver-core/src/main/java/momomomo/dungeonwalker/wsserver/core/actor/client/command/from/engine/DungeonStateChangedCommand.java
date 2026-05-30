package momomomo.dungeonwalker.wsserver.core.actor.client.command.from.engine;

import lombok.NonNull;
import momomomo.dungeonwalker.wsserver.core.actor.client.command.ClientCommand;
import momomomo.dungeonwalker.wsserver.domain.data.common.DungeonCoordinates;

import java.util.Map;

public record DungeonStateChangedCommand(@NonNull Map<String, DungeonCoordinates> dungeonState) implements ClientCommand {
}

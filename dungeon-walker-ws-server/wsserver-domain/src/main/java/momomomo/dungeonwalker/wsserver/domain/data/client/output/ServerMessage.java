package momomomo.dungeonwalker.wsserver.domain.data.client.output;

import lombok.NonNull;

public record ServerMessage(@NonNull String message) implements OutputData {
}

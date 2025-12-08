package momomomo.dungeonwalker.wsserver.domain.output;

import lombok.NonNull;

public record ServerMessage(@NonNull String message) implements OutputData {
}

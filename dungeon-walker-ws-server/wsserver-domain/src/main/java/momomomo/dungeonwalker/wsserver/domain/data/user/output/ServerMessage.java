package momomomo.dungeonwalker.wsserver.domain.data.user.output;

import lombok.NonNull;

public record ServerMessage(@NonNull String message) implements OutputData {
}

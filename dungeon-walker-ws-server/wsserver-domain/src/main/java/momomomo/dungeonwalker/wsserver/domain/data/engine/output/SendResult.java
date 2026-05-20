package momomomo.dungeonwalker.wsserver.domain.data.engine.output;

import lombok.NonNull;

public record SendResult(@NonNull SendStatus status, @NonNull String message) {
}

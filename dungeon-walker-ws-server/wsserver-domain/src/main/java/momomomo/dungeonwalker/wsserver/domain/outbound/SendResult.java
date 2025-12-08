package momomomo.dungeonwalker.wsserver.domain.outbound;

import lombok.NonNull;

public record SendResult(@NonNull SendStatus status, @NonNull String message) {
}

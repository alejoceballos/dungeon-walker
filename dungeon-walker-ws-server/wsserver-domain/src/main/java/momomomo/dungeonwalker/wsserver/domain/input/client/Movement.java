package momomomo.dungeonwalker.wsserver.domain.input.client;

import jakarta.annotation.Nullable;
import lombok.NonNull;

public record Movement(
        @Nullable String clientId,
        @NonNull Direction direction
) implements InputData {
}

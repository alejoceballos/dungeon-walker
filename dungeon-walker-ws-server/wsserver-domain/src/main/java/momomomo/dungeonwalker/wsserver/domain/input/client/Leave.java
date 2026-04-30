package momomomo.dungeonwalker.wsserver.domain.input.client;

import jakarta.annotation.Nullable;

public record Leave(@Nullable String clientId) implements InputData {
}

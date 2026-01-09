package momomomo.dungeonwalker.wsserver.domain.input;

import jakarta.annotation.Nullable;

public record Identity(@Nullable String clientId) implements InputData {
}

package momomomo.dungeonwalker.wsserver.domain.input;

import lombok.NonNull;

public record Identity(@NonNull String id) implements InputData {
}

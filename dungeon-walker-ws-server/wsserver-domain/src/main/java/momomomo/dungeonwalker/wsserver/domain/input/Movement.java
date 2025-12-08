package momomomo.dungeonwalker.wsserver.domain.input;

import lombok.NonNull;

public record Movement(@NonNull Direction direction) implements InputData {
}

package momomomo.dungeonwalker.wsserver.domain.data.client.input;

import lombok.NonNull;

public record Movement(@NonNull Direction direction) implements InputData {
}

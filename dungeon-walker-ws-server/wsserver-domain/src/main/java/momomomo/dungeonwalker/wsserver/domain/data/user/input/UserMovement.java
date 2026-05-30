package momomomo.dungeonwalker.wsserver.domain.data.user.input;

import lombok.NonNull;

public record UserMovement(@NonNull Direction direction) implements InputData {
}

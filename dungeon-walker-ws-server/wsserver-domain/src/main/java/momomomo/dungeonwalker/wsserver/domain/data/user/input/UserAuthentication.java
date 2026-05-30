package momomomo.dungeonwalker.wsserver.domain.data.user.input;

import lombok.NonNull;

public record UserAuthentication(@NonNull String token) implements InputData {
}

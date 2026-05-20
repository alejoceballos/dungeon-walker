package momomomo.dungeonwalker.wsserver.domain.data.client.input;

import lombok.NonNull;

public record Authenticate(@NonNull String credentials) implements InputData {
}

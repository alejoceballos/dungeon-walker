package momomomo.dungeonwalker.wsserver.domain.data.user.output;

import lombok.NonNull;

import java.util.List;

public record ClientErrors(@NonNull List<String> errors) implements OutputData {
}

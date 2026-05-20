package momomomo.dungeonwalker.wsserver.domain.data.client.output;

import lombok.NonNull;

import java.util.List;

public record ServerErrors(@NonNull List<String> errors) implements OutputData {
}

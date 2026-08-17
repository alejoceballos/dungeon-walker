package momomomo.dungeonwalker.history.domain.inbound;

import lombok.NonNull;

public record HistoryLog(@NonNull String id, int x, int y) {
}

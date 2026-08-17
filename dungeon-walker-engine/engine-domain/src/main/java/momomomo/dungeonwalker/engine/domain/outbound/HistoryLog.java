package momomomo.dungeonwalker.engine.domain.outbound;

import lombok.NonNull;

public record HistoryLog(@NonNull String id, int x, int y) {
}

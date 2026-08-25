package momomomo.dungeonwalker.history.domain.outbound.data;

import java.time.OffsetDateTime;

public record HistoryOutput(OffsetDateTime timestamp, int x, int y) {
}

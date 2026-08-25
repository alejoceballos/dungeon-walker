package momomomo.dungeonwalker.history.domain.outbound.data;

import java.util.List;

public record WalkerOutput(String name, List<HistoryOutput> history) {
}

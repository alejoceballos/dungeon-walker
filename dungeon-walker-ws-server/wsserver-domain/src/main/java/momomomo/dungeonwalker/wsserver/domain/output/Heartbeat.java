package momomomo.dungeonwalker.wsserver.domain.output;

import lombok.Builder;
import lombok.NonNull;

@Builder
public record Heartbeat(
        long timestamp,
        int delay,
        @NonNull String timeUnit
) implements OutputData {
}

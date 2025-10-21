package momomomo.dungeonwalker.wsserver.domain.output;

import lombok.Builder;

@Builder
public record Heartbeat(
        long timestamp,
        int delay,
        String timeUnit
) implements OutputData {
}

package momomomo.dungeonwalker.wsserver.domain.output;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.NonNull;

import java.time.Instant;

public record ServerHeartbeat(
        int delay,
        @NonNull String timeUnit,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC") @NonNull Instant timestamp
) implements OutputData {
}

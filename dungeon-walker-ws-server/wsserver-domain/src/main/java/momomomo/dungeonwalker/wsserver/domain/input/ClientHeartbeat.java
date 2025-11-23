package momomomo.dungeonwalker.wsserver.domain.input;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.NonNull;

import java.time.Instant;

public record ClientHeartbeat(
        @NonNull String id,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC") @NonNull Instant timestamp
) implements InputData {
}

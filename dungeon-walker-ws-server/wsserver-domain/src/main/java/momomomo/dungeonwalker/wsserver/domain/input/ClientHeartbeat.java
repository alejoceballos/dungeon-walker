package momomomo.dungeonwalker.wsserver.domain.input;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.annotation.Nullable;
import lombok.NonNull;

import java.time.Instant;

public record ClientHeartbeat(
        @Nullable String clientId,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
        @NonNull Instant timestamp
) implements InputData {
}

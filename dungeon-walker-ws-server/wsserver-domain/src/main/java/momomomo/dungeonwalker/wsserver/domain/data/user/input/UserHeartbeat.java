package momomomo.dungeonwalker.wsserver.domain.data.user.input;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.NonNull;

import java.time.Instant;

public record UserHeartbeat(
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
        @NonNull Instant timestamp
) implements InputData {
}

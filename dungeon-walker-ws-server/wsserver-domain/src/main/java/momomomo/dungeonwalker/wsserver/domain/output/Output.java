package momomomo.dungeonwalker.wsserver.domain.output;

import lombok.Builder;
import lombok.NonNull;

@Builder
public record Output(
        @NonNull String type,
        @NonNull OutputData data
) {
}

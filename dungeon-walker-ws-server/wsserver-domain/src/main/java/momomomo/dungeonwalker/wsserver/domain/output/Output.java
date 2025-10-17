package momomomo.dungeonwalker.wsserver.domain.output;

import lombok.Builder;

@Builder
public record Output(
        String sessionId,
        String type,
        OutputData data
) {
}

package momomomo.dungeonwalker.wsserver.domain.output;

import lombok.Builder;

@Builder
public record MapSetup(
        int width,
        int height
) implements OutputData {
}

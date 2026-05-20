package momomomo.dungeonwalker.wsserver.core.inbound.client.handler;

import jakarta.annotation.Nullable;
import lombok.NonNull;
import momomomo.dungeonwalker.wsserver.domain.data.client.input.InputData;

public record HandlerContext(@Nullable String clientOutboundId, @NonNull InputData data) {
}

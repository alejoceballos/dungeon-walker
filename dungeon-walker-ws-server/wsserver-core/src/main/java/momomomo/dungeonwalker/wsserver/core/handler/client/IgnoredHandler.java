package momomomo.dungeonwalker.wsserver.core.handler.client;

import jakarta.annotation.Nonnull;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.wsserver.domain.input.InputData;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static momomomo.dungeonwalker.wsserver.core.handler.client.HandlingResult.Type.FAILURE;

@Slf4j
public class IgnoredHandler implements ClientDataHandler<InputData> {

    @Nonnull
    @Override
    public CompletableFuture<HandlingResult> handle(@NonNull final String clientId, @NonNull final InputData inputData) {
        log.debug("---> [DATA HANDLER - {}] Ignoring input data: {}", this.getClass().getSimpleName(), inputData);
        return CompletableFuture.completedFuture(HandlingResult.builder()
                .type(FAILURE)
                .errors(List.of("This type of data is not yet supported"))
                .build());
    }

}

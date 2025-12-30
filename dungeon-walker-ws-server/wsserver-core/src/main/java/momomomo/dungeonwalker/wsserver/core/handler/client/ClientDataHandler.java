package momomomo.dungeonwalker.wsserver.core.handler.client;

import jakarta.annotation.Nonnull;
import momomomo.dungeonwalker.wsserver.domain.input.InputData;

import java.util.concurrent.CompletableFuture;

public interface ClientDataHandler<I extends InputData> {

    @Nonnull
    CompletableFuture<HandlingResult> handle(@Nonnull String clientId, @Nonnull I data);

}

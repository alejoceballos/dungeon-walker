package momomomo.dungeonwalker.wsserver.core.handler;

import jakarta.annotation.Nonnull;
import momomomo.dungeonwalker.wsserver.domain.input.InputData;

import java.util.concurrent.CompletableFuture;

public interface DataHandler<I extends InputData> {

    @Nonnull
    CompletableFuture<HandlingResult> handle(@Nonnull String clientId, @Nonnull I data);

}

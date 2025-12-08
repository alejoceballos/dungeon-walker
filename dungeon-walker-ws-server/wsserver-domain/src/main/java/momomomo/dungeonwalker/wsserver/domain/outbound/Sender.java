package momomomo.dungeonwalker.wsserver.domain.outbound;

import jakarta.annotation.Nonnull;

import java.util.concurrent.CompletableFuture;

public interface Sender<M> {

    @Nonnull
    CompletableFuture<SendResult> send(@Nonnull M message);

}

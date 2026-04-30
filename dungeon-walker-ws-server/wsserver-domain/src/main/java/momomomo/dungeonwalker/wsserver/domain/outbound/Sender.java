package momomomo.dungeonwalker.wsserver.domain.outbound;

import lombok.NonNull;

import java.util.concurrent.CompletableFuture;

public interface Sender<M> {

    @NonNull
    CompletableFuture<SendResult> send(@NonNull M message);

}

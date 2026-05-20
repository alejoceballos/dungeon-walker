package momomomo.dungeonwalker.wsserver.domain.data.engine.output;

import lombok.NonNull;

import java.util.concurrent.CompletableFuture;

public interface EngineOutbound<M> {

    @NonNull
    CompletableFuture<SendResult> send(@NonNull M message);

}

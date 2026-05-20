package momomomo.dungeonwalker.wsserver.core.inbound.client.handler.ignored;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.wsserver.core.inbound.client.handler.HandlerContext;
import momomomo.dungeonwalker.wsserver.domain.handler.HandlingResult;
import momomomo.dungeonwalker.wsserver.domain.handler.MessageHandler;

import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.completedFuture;

@Slf4j
public class IgnoreClientDataHandler implements MessageHandler<HandlerContext, CompletableFuture<HandlingResult>> {

    private static final String LABEL = "---> [IGNORED CLIENT DATA HANDLER -";

    @Override
    public @NonNull CompletableFuture<HandlingResult> handle(@NonNull final HandlerContext context) {
        log.debug("{} {}] Data: {}", LABEL, this.getClass().getSimpleName(), context.data());

        return completedFuture(HandlingResult.failure("This type of data is not yet supported"));
    }
}

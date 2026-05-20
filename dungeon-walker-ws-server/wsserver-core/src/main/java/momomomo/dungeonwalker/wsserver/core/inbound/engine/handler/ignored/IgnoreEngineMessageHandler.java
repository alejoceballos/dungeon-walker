package momomomo.dungeonwalker.wsserver.core.inbound.engine.handler.ignored;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.contract.engine.EngineMessageProto.EngineMessage;
import momomomo.dungeonwalker.wsserver.domain.handler.HandlingResult;
import momomomo.dungeonwalker.wsserver.domain.handler.MessageHandler;

import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.completedFuture;

@Slf4j
public class IgnoreEngineMessageHandler implements MessageHandler<EngineMessage, CompletableFuture<HandlingResult>> {

    private static final String LABEL = "---> [IGNORED ENGINE MESSAGE HANDLER -";

    @Override
    public @NonNull CompletableFuture<HandlingResult> handle(@NonNull final EngineMessage message) {
        log.debug("{} {}] Data: {}", LABEL, this.getClass().getSimpleName(), message);

        return completedFuture(HandlingResult.ignored("This type of data is not yet supported"));
    }
}

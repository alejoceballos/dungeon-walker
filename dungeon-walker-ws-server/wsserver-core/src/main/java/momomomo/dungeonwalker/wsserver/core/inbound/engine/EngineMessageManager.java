package momomomo.dungeonwalker.wsserver.core.inbound.engine;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.contract.engine.EngineMessageProto.EngineMessage;
import momomomo.dungeonwalker.wsserver.core.inbound.engine.handler.EngineInputHandlerSelector;
import momomomo.dungeonwalker.wsserver.domain.handler.HandlingResult;
import momomomo.dungeonwalker.wsserver.domain.inbound.EngineInbound;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class EngineMessageManager implements EngineInbound<EngineMessage, CompletableFuture<HandlingResult>> {

    private static final String LABEL = "---> [ENGINE INBOUND - Manager]";

    private final EngineInputHandlerSelector handlerSelector;

    @Override
    public CompletableFuture<HandlingResult> handleMessage(@NonNull final EngineMessage message) {
        log.debug("{} Handling message", LABEL);
        return handlerSelector
                .select(message)
                .handle(message);
    }

}

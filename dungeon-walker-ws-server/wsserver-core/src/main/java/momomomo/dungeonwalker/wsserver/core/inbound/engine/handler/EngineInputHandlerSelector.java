package momomomo.dungeonwalker.wsserver.core.inbound.engine.handler;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.contract.engine.EngineMessageProto.EngineMessage;
import momomomo.dungeonwalker.wsserver.core.inbound.engine.handler.ignored.IgnoreEngineMessageHandler;
import momomomo.dungeonwalker.wsserver.domain.handler.HandlingResult;
import momomomo.dungeonwalker.wsserver.domain.handler.MessageHandler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Predicate;

@Slf4j
@Component
@RequiredArgsConstructor
public class EngineInputHandlerSelector {

    private static final String LABEL = "---> [ENGINE INPUT HANDLER SELECTOR]";
    private final List<SelectableEngineInputHandler> handlers;

    public MessageHandler<EngineMessage, CompletableFuture<HandlingResult>>
    select(@NonNull final EngineMessage message) {
        log.debug("{} Selecting message handler for input: {}", LABEL, message);

        final var dataHandler = handlers
                .stream()
                .filter(byCanHandleData(message))
                .findAny()
                .map(toMessageHandler())
                .orElseGet(IgnoreEngineMessageHandler::new);

        log.debug("{} Selected message handler: {}", LABEL, dataHandler.getClass().getSimpleName());

        return dataHandler;
    }

    private static @NonNull Predicate<SelectableEngineInputHandler>
    byCanHandleData(@NonNull final EngineMessage message) {
        return handler -> handler.canHandle(message);
    }

    private static @NonNull
    Function<SelectableEngineInputHandler, MessageHandler<EngineMessage, CompletableFuture<HandlingResult>>>
    toMessageHandler() {
        return handler -> (MessageHandler<EngineMessage, CompletableFuture<HandlingResult>>) handler;
    }

}

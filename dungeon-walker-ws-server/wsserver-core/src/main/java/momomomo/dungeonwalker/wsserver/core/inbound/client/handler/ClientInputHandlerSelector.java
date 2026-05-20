package momomomo.dungeonwalker.wsserver.core.inbound.client.handler;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.wsserver.core.inbound.client.handler.ignored.IgnoreClientDataHandler;
import momomomo.dungeonwalker.wsserver.domain.data.client.input.InputData;
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
public class ClientInputHandlerSelector {

    private static final String LABEL = "---> [CLIENT INPUT HANDLER SELECTOR]";
    private final List<SelectableClientInputHandler<? extends InputData>> handlers;

    public <I extends InputData> MessageHandler<HandlerContext, CompletableFuture<HandlingResult>>
    select(@NonNull final I message) {
        log.debug("{} Selecting message handler for input: {}", LABEL, message);

        final var dataHandler = handlers
                .stream()
                .filter(byCanHandleData(message))
                .findAny()
                .map(toMessageHandler())
                .orElseGet(IgnoreClientDataHandler::new);

        log.debug("{} Selected message handler: {}", LABEL, dataHandler.getClass().getSimpleName());

        return dataHandler;
    }

    private static <I extends InputData>
    @NonNull Predicate<SelectableClientInputHandler<? extends InputData>>
    byCanHandleData(@NonNull final I data) {
        return handler -> handler.canHandle(data);
    }

    private static @NonNull
    Function<SelectableClientInputHandler<? extends InputData>, MessageHandler<HandlerContext, CompletableFuture<HandlingResult>>>
    toMessageHandler() {
        return handler ->
                (MessageHandler<HandlerContext, CompletableFuture<HandlingResult>>) handler;
    }

}

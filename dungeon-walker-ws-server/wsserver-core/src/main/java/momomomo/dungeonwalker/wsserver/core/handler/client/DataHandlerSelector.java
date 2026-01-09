package momomomo.dungeonwalker.wsserver.core.handler.client;

import jakarta.annotation.Nonnull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.contract.client.ClientRequestProto.ClientRequest;
import momomomo.dungeonwalker.wsserver.domain.handler.MessageHandler;
import momomomo.dungeonwalker.wsserver.domain.input.InputData;
import momomomo.dungeonwalker.wsserver.domain.outbound.Sender;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Predicate;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataHandlerSelector {

    private final List<SelectableInputDataHandler<? extends InputData>> handlers;

    public <I extends InputData>
    MessageHandler<InputData, Sender<ClientRequest>, CompletableFuture<HandlingResult>>
    select(@NonNull final I message) {
        log.debug("---> [DATA HANDLER SELECTOR] Selecting message handler for input: {}", message);

        final var dataHandler = handlers.stream()
                .filter(byCanHandleData(message))
                .findFirst()
                .map(toMessageHandler())
                .orElseGet(IgnoredHandler::new);

        log.debug("---> [DATA HANDLER SELECTOR] Selected message handler: {}", dataHandler.getClass().getSimpleName());

        return dataHandler;
    }

    private static <I extends InputData>
    @Nonnull Predicate<SelectableInputDataHandler<? extends InputData>>
    byCanHandleData(@NonNull I data) {
        return handler -> handler.canHandle(data);
    }

    @SuppressWarnings("unchecked")
    private static @Nonnull
    Function<SelectableInputDataHandler<? extends InputData>,
            MessageHandler<InputData,
                    Sender<ClientRequest>,
                    CompletableFuture<HandlingResult>>>
    toMessageHandler() {
        return handler ->
                (MessageHandler<InputData, Sender<ClientRequest>, CompletableFuture<HandlingResult>>) handler;
    }

}

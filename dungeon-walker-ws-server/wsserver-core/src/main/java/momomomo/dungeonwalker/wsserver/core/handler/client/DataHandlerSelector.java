package momomomo.dungeonwalker.wsserver.core.handler.client;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.contract.client.ClientRequestProto.ClientRequest;
import momomomo.dungeonwalker.wsserver.domain.handler.MessageHandler;
import momomomo.dungeonwalker.wsserver.domain.input.client.InputData;
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

    private static final String LABEL = "---> [DATA HANDLER SELECTOR]";
    private final List<SelectableInputDataHandler<? extends InputData>> handlers;

    public <I extends InputData>
    MessageHandler<InputData, Sender<ClientRequest>, CompletableFuture<HandlingResult>>
    select(@NonNull final I message) {
        log.debug("{} Selecting message handler for input: {}", LABEL, message);

        final var dataHandler = handlers.stream()
                .filter(byCanHandleData(message))
                .findFirst()
                .map(toMessageHandler())
                .orElseGet(IgnoredHandler::new);

        log.debug("{} Selected message handler: {}", LABEL, dataHandler.getClass().getSimpleName());

        return dataHandler;
    }

    private static <I extends InputData>
    @NonNull Predicate<SelectableInputDataHandler<? extends InputData>>
    byCanHandleData(@NonNull final I data) {
        return handler -> handler.canHandle(data);
    }

    @SuppressWarnings("unchecked")
    private static @NonNull
    Function<SelectableInputDataHandler<? extends InputData>,
            MessageHandler<InputData,
                    Sender<ClientRequest>,
                    CompletableFuture<HandlingResult>>>
    toMessageHandler() {
        return handler ->
                (MessageHandler<InputData, Sender<ClientRequest>, CompletableFuture<HandlingResult>>) handler;
    }

}

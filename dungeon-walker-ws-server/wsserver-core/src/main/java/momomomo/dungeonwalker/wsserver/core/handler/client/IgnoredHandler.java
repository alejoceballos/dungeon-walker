package momomomo.dungeonwalker.wsserver.core.handler.client;

import jakarta.annotation.Nonnull;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.contract.client.ClientRequestProto;
import momomomo.dungeonwalker.wsserver.domain.handler.MessageHandler;
import momomomo.dungeonwalker.wsserver.domain.input.InputData;
import momomomo.dungeonwalker.wsserver.domain.outbound.Sender;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static momomomo.dungeonwalker.wsserver.core.handler.client.HandlingResult.Type.FAILURE;

@Slf4j
public class IgnoredHandler implements MessageHandler<InputData, Sender<ClientRequestProto.ClientRequest>, CompletableFuture<HandlingResult>> {

    private static final String LABEL = "---> [DATA HANDLER -";

    @Override
    public @Nonnull CompletableFuture<HandlingResult> handle(
            @NonNull final InputData message,
            @NonNull final Sender<ClientRequestProto.ClientRequest> unused
    ) {
        log.debug("{} {}] Data: {}", LABEL, this.getClass().getSimpleName(), message);

        return completedFuture(HandlingResult
                .builder()
                .type(FAILURE)
                .errors(List.of("This type of data is not yet supported"))
                .build());
    }
}

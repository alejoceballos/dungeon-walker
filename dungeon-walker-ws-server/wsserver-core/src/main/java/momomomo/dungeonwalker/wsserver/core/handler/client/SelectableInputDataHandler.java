package momomomo.dungeonwalker.wsserver.core.handler.client;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.wsserver.core.mapper.InputDataMapper;
import momomomo.dungeonwalker.wsserver.core.validator.InputDataValidator;
import momomomo.dungeonwalker.wsserver.core.validator.ValidationError;
import momomomo.dungeonwalker.wsserver.domain.input.InputData;
import momomomo.dungeonwalker.wsserver.domain.outbound.SendResult;
import momomomo.dungeonwalker.wsserver.domain.outbound.SendStatus;
import momomomo.dungeonwalker.wsserver.domain.outbound.Sender;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static momomomo.dungeonwalker.wsserver.core.handler.client.HandlingResult.Type.FAILURE;
import static momomomo.dungeonwalker.wsserver.core.handler.client.HandlingResult.Type.SUCCESS;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

@Slf4j
@RequiredArgsConstructor
public abstract class SelectableInputDataHandler<I extends InputData, P> implements ClientDataHandler<I> {

    private final InputDataMapper<I, P> mapper;
    private final InputDataValidator<I> validator;
    private final Sender<P> sender;

    @Nonnull
    @Override
    public CompletableFuture<HandlingResult> handle(@NonNull final String clientId, @NonNull final I inputData) {
        log.debug("---> [DATA HANDLER - {}] Handling input data: {}", this.getClass().getSimpleName(), inputData);
        final var errors = validator.validate(inputData);

        if (isNotEmpty(errors)) {
            return CompletableFuture.completedFuture(
                    HandlingResult.builder()
                            .type(FAILURE)
                            .errors(errors.stream()
                                    .map(ValidationError::toString)
                                    .toList())
                            .build());
        }

        final var mappedData = mapper.map(clientId, inputData);

        return sender.send(mappedData)
                .thenCompose(result -> CompletableFuture.completedFuture(map(result)))
                .exceptionally(ex -> HandlingResult.builder()
                        .type(FAILURE)
                        .errors(List.of(ex.getMessage()))
                        .build());
    }

    private HandlingResult map(@NonNull final SendResult result) {
        final var type = result.status() == SendStatus.SUCCESS ? SUCCESS : FAILURE;
        final List<String> errors = result.status() == SendStatus.FAILURE ? List.of(result.message()) : List.of();
        return HandlingResult.builder().type(type).errors(errors).build();
    }

    protected abstract boolean isSelected(@Nullable final InputData data);

}

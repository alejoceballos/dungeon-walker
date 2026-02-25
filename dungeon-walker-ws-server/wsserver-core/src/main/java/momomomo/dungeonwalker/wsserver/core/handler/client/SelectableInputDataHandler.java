package momomomo.dungeonwalker.wsserver.core.handler.client;

import jakarta.annotation.Nonnull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.commons.conditional.Conditional;
import momomomo.dungeonwalker.contract.client.ClientRequestProto.ClientRequest;
import momomomo.dungeonwalker.wsserver.core.mapper.InputDataMapper;
import momomomo.dungeonwalker.wsserver.core.validator.InputDataValidator;
import momomomo.dungeonwalker.wsserver.core.validator.ValidationError;
import momomomo.dungeonwalker.wsserver.domain.handler.MessageHandler;
import momomomo.dungeonwalker.wsserver.domain.input.InputData;
import momomomo.dungeonwalker.wsserver.domain.outbound.SendResult;
import momomomo.dungeonwalker.wsserver.domain.outbound.SendStatus;
import momomomo.dungeonwalker.wsserver.domain.outbound.Sender;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static momomomo.dungeonwalker.wsserver.core.handler.client.HandlingResult.Type.FAILURE;
import static momomomo.dungeonwalker.wsserver.core.handler.client.HandlingResult.Type.SUCCESS;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;

@Slf4j
@RequiredArgsConstructor
public abstract class SelectableInputDataHandler<I extends InputData>
        implements MessageHandler<I, Sender<ClientRequest>, CompletableFuture<HandlingResult>> {

    private static final String LABEL = "---> [DATA HANDLER -";

    private final InputDataMapper<I, ClientRequest> mapper;
    private final InputDataValidator<I> validator;

    @Override
    @Nonnull
    public CompletableFuture<HandlingResult> handle(
            @NonNull final I inputData,
            @NonNull final Sender<ClientRequest> sender
    ) {
        log.debug("{} {}] Data: {}", LABEL, this.getClass().getSimpleName(), inputData);
        final var errors = validator.validate(inputData);

        return Conditional
                .on(() -> isEmpty(errors))
                .thenGet(() -> sender
                        .send(mapper.map(inputData))
                        .thenCompose(result -> completedFuture(map(result)))
                        .exceptionally(ex -> HandlingResult.builder()
                                .type(FAILURE)
                                .errors(List.of(ex.getMessage()))
                                .build()))
                .orElseGet(() -> completedFuture(
                        HandlingResult.builder()
                                .type(FAILURE)
                                .errors(errors.stream()
                                        .map(ValidationError::toString)
                                        .toList())
                                .build()))
                .evaluate();
    }

    protected abstract boolean canHandle(@NonNull InputData message);

    private HandlingResult map(@NonNull final SendResult result) {
        final var type = result.status() == SendStatus.SUCCESS ? SUCCESS : FAILURE;
        final List<String> errors = result.status() == SendStatus.FAILURE ? List.of(result.message()) : List.of();

        return HandlingResult
                .builder()
                .type(type)
                .errors(errors)
                .build();
    }

}

package momomomo.dungeonwalker.wsserver.core.handler;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.wsserver.core.mapper.InputDataMapper;
import momomomo.dungeonwalker.wsserver.core.validator.InputDataValidator;
import momomomo.dungeonwalker.wsserver.domain.input.InputData;
import momomomo.dungeonwalker.wsserver.domain.outbound.Sender;

import static momomomo.dungeonwalker.wsserver.core.handler.HandlingResult.type.SUCCESS;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

@Slf4j
@RequiredArgsConstructor
public abstract class SelectableInputDataHandler<I extends InputData, P> implements DataHandler<I> {

    private final InputDataMapper<I, P> mapper;
    private final InputDataValidator<I> validator;
    private final Sender<P> sender;

    @Nonnull
    @Override
    public HandlingResult handle(final I inputData) {
        log.debug("---> [DATA HANDLER - {}] Handling input data: {}", this.getClass().getSimpleName(), inputData);
        final var errors = validator.validate(inputData);

        if (isNotEmpty(errors)) {
            return HandlingResult.builder()
                    .type(SUCCESS)
                    .errors(errors)
                    .build();
        }

        final var mappedData = mapper.map(inputData);
        sender.send(mappedData);

        return HandlingResult.builder()
                .type(SUCCESS)
                .build();
    }

    protected abstract boolean isSelected(InputData data);

}

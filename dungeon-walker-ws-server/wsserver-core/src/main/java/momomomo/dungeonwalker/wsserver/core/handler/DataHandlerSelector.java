package momomomo.dungeonwalker.wsserver.core.handler;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.wsserver.domain.input.InputData;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataHandlerSelector {

    private final List<SelectableInputDataHandler<? extends InputData, ?>> handlers;

    @SuppressWarnings("unchecked")
    public <I extends InputData> DataHandler<I> select(@NonNull final I data) {
        log.debug("---> [DATA HANDLER SELECTOR] Selecting data handler for input: {}", data);

        final var dataHandler = handlers.stream()
                .filter(handler -> handler.isSelected(data))
                .findFirst()
                .map(handler -> (DataHandler<I>) handler)
                .orElse((DataHandler<I>) new IgnoredHandler());

        log.debug("---> [DATA HANDLER SELECTOR] Selected data handler: {}", dataHandler.getClass().getSimpleName());

        return dataHandler;
    }

}

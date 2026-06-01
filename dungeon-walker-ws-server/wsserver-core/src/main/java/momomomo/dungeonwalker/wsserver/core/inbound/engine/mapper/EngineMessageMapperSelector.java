package momomomo.dungeonwalker.wsserver.core.inbound.engine.mapper;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.contract.engine.EngineMessageProto.EngineMessage;
import momomomo.dungeonwalker.wsserver.core.actor.client.command.ClientCommand;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

@Slf4j
@Component
@RequiredArgsConstructor
public class EngineMessageMapperSelector {

    private static final String LABEL = "---> [ENGINE INPUT MAPPER SELECTOR]";

    private final List<EngineMessageMapper<? extends ClientCommand>> mappers;

    public EngineMessageMapper<ClientCommand> select(@NonNull final EngineMessage message) {
        log.debug("{} Selecting engine mapper for input: {}", LABEL, message);

        final var mapper = mappers
                .stream()
                .filter(byCanMap(message))
                .findAny()
                .map(toGenericAbstractType())
                .orElse(null);

        log.debug("{} Selected mapper: {}",
                LABEL,
                Optional.ofNullable(mapper)
                        .map(Object::getClass)
                        .map(Class::getSimpleName)
                        .orElse("null"));

        return mapper;
    }

    private static @NonNull Predicate<EngineMessageMapper<? extends ClientCommand>> byCanMap(
            @NonNull final EngineMessage message
    ) {
        return mapper -> mapper.canMap(message);
    }

    @SuppressWarnings("unchecked")
    private static @NonNull Function<EngineMessageMapper<? extends ClientCommand>, EngineMessageMapper<ClientCommand>>
    toGenericAbstractType() {
        return mapper -> (EngineMessageMapper<ClientCommand>) mapper;
    }

}

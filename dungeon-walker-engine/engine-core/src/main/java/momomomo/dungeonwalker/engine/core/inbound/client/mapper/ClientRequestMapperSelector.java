package momomomo.dungeonwalker.engine.core.inbound.client.mapper;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.contract.client.ClientRequestProto.ClientRequest;
import momomomo.dungeonwalker.engine.core.actor.walker.command.WalkerCommand;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClientRequestMapperSelector {

    private static final String LABEL = "---> [CLIENT REQUEST MAPPER SELECTOR]";

    private final List<ClientRequestMapper<? extends WalkerCommand>> mappers;

    public ClientRequestMapper<WalkerCommand> select(@NonNull final ClientRequest request) {
        log.debug("{} Selecting message handler for request: {}", LABEL, request);

        final var mapper = mappers
                .stream()
                .filter(byCanMap(request))
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

    private static @NonNull Predicate<ClientRequestMapper<? extends WalkerCommand>> byCanMap(
            @NonNull final ClientRequest request
    ) {
        return mapper -> mapper.canMap(request);
    }

    @SuppressWarnings("unchecked")
    private static @NonNull Function<ClientRequestMapper<? extends WalkerCommand>, ClientRequestMapper<WalkerCommand>>
    toGenericAbstractType() {
        return mapper -> (ClientRequestMapper<WalkerCommand>) mapper;
    }

}

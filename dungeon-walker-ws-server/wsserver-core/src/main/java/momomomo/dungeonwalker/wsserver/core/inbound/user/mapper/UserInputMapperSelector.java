package momomomo.dungeonwalker.wsserver.core.inbound.user.mapper;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.ConnectionCommand;
import momomomo.dungeonwalker.wsserver.domain.data.user.input.InputData;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserInputMapperSelector {

    private static final String LABEL = "---> [USER INPUT MAPPER SELECTOR]";
    private final List<UserInputMapper<? extends InputData, ? extends ConnectionCommand>> mappers;

    public UserInputMapper<InputData, ConnectionCommand> select(@NonNull final InputData input) {
        log.debug("{} Selecting user mapper for input: {}", LABEL, input);

        final var mapper = mappers
                .stream()
                .filter(byCanMap(input))
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

    private @NonNull Predicate<UserInputMapper<? extends InputData, ? extends ConnectionCommand>> byCanMap(
            @NonNull final InputData input
    ) {
        return mapper -> mapper.canMap(input);
    }

    @SuppressWarnings("unchecked")
    private static @NonNull Function<UserInputMapper<? extends InputData, ? extends ConnectionCommand>, UserInputMapper<InputData, ConnectionCommand>>
    toGenericAbstractType() {
        return mapper -> (UserInputMapper<InputData, ConnectionCommand>) mapper;
    }

}

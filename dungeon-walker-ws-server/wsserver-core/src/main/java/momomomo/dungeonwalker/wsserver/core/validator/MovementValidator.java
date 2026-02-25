package momomomo.dungeonwalker.wsserver.core.validator;

import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.wsserver.domain.input.Movement;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class MovementValidator implements InputDataValidator<Movement> {

    private static final String LABEL = "---> [VALIDATOR - Identity]";

    @Nonnull
    @Override
    public List<ValidationError> validate(@Nonnull final Movement inputData) {
        log.debug("{} validating \"{}\"", LABEL, inputData);
        return List.of();
    }
}

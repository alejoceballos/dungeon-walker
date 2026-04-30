package momomomo.dungeonwalker.wsserver.core.handler.client.movement;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.wsserver.core.handler.client.InputDataValidator;
import momomomo.dungeonwalker.wsserver.core.handler.client.ValidationError;
import momomomo.dungeonwalker.wsserver.domain.input.client.Movement;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class MovementValidator implements InputDataValidator<Movement> {

    private static final String LABEL = "---> [VALIDATOR - Identity]";

    @NonNull
    @Override
    public List<ValidationError> validate(@NonNull final Movement inputData) {
        log.debug("{} validating \"{}\"", LABEL, inputData);
        return List.of();
    }
}

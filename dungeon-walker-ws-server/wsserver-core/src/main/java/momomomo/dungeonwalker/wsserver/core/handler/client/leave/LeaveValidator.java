package momomomo.dungeonwalker.wsserver.core.handler.client.leave;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.commons.conditional.Conditional;
import momomomo.dungeonwalker.wsserver.core.handler.client.InputDataValidator;
import momomomo.dungeonwalker.wsserver.core.handler.client.ValidationError;
import momomomo.dungeonwalker.wsserver.domain.input.client.Leave;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static momomomo.dungeonwalker.wsserver.core.handler.client.ValidationError.Type.NOT_EMPTY;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Component
public class LeaveValidator implements InputDataValidator<Leave> {

    private static final String LABEL = "---> [VALIDATOR - Leave] ";

    @NonNull
    @Override
    public List<ValidationError> validate(@NonNull final Leave inputData) {
        log.debug("{} validating \"{}\"", LABEL, inputData);

        final var errors = new ArrayList<ValidationError>();

        Conditional
                .on(() -> isBlank(inputData.clientId()))
                .thenGet(() -> errors.add(new ValidationError("clientId", NOT_EMPTY)))
                .evaluate();

        return errors;
    }
}

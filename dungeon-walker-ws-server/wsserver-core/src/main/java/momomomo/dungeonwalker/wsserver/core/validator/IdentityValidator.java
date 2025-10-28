package momomomo.dungeonwalker.wsserver.core.validator;

import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.wsserver.domain.input.Identity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;
import static momomomo.dungeonwalker.wsserver.core.validator.ValidationError.Type.NOT_EMPTY;
import static momomomo.dungeonwalker.wsserver.core.validator.ValidationError.Type.NOT_NULL;
import static org.apache.commons.lang3.StringUtils.isEmpty;

@Slf4j
@Component
public class IdentityValidator implements InputDataValidator<Identity> {

    @Nonnull
    @Override
    public List<ValidationError> validate(final Identity inputData) {
        log.debug("[VALIDATOR - Identity] validating \"{}\"", inputData);
        
        final var errors = new ArrayList<ValidationError>();

        if (isNull(inputData.id())) {
            errors.add(new ValidationError("id", NOT_NULL));

        } else if (isEmpty(inputData.id())) {
            errors.add(new ValidationError("id", NOT_EMPTY));
        }

        return errors;
    }
}

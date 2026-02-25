package momomomo.dungeonwalker.wsserver.core.validator;

import jakarta.annotation.Nonnull;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.wsserver.domain.input.Identity;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class IdentityValidator implements InputDataValidator<Identity> {

    private static final String LABEL = "---> [VALIDATOR - Identity] ";

    @Nonnull
    @Override
    public List<ValidationError> validate(@NonNull final Identity inputData) {
        log.debug("{} validating \"{}\"", LABEL, inputData);
        return List.of();
    }
}

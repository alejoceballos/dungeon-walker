package momomomo.dungeonwalker.wsserver.core.validator;

import jakarta.annotation.Nonnull;
import momomomo.dungeonwalker.wsserver.domain.input.InputData;

import java.util.List;

public interface InputDataValidator<I extends InputData> {

    @Nonnull
    List<ValidationError> validate(I inputData);

}

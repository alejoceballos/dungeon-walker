package momomomo.dungeonwalker.wsserver.core.handler.client;

import lombok.NonNull;
import momomomo.dungeonwalker.wsserver.domain.input.client.InputData;

import java.util.List;

public interface InputDataValidator<I extends InputData> {

    @NonNull
    List<ValidationError> validate(@NonNull I inputData);

}

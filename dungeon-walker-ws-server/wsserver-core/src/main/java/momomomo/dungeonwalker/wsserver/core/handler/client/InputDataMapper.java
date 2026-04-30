package momomomo.dungeonwalker.wsserver.core.handler.client;

import lombok.NonNull;
import momomomo.dungeonwalker.wsserver.domain.input.client.InputData;

public interface InputDataMapper<I extends InputData, P> {

    @NonNull
    P map(@NonNull I input);

}

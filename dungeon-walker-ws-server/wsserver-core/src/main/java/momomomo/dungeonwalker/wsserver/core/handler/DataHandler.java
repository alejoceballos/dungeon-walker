package momomomo.dungeonwalker.wsserver.core.handler;

import jakarta.annotation.Nonnull;
import momomomo.dungeonwalker.wsserver.domain.input.InputData;

public interface DataHandler<I extends InputData> {

    @Nonnull
    HandlingResult handle(I data);

}

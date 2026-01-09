package momomomo.dungeonwalker.wsserver.core.mapper;

import jakarta.annotation.Nonnull;
import momomomo.dungeonwalker.wsserver.domain.input.InputData;

public interface InputDataMapper<I extends InputData, P> {

    @Nonnull
    P map(@Nonnull I input);

}

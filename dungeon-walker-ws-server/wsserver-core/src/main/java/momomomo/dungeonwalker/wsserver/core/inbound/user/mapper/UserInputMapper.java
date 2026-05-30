package momomomo.dungeonwalker.wsserver.core.inbound.user.mapper;

import lombok.NonNull;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.ConnectionCommand;
import momomomo.dungeonwalker.wsserver.domain.data.user.input.InputData;

public interface UserInputMapper<I extends InputData, O extends ConnectionCommand> {

    O map(@NonNull final I input);

    boolean canMap(@NonNull InputData input);

}

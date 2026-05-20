package momomomo.dungeonwalker.wsserver.core.inbound.client.handler;

import lombok.NonNull;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.ConnectionCommand;
import momomomo.dungeonwalker.wsserver.domain.data.client.input.InputData;

public interface ClientInputMapper<I extends InputData, P extends ConnectionCommand> {

    @NonNull
    P map(@NonNull I input);

}

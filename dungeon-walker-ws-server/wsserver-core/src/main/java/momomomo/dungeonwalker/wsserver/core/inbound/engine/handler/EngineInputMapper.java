package momomomo.dungeonwalker.wsserver.core.inbound.engine.handler;

import lombok.NonNull;
import momomomo.dungeonwalker.contract.engine.EngineMessageProto.EngineMessage;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.ConnectionCommand;

public interface EngineInputMapper<P extends ConnectionCommand> {

    @NonNull
    P map(@NonNull EngineMessage input);

}

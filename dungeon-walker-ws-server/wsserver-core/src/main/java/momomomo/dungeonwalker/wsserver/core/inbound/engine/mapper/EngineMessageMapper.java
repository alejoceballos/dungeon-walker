package momomomo.dungeonwalker.wsserver.core.inbound.engine.mapper;

import lombok.NonNull;
import momomomo.dungeonwalker.contract.engine.EngineMessageProto.EngineMessage;
import momomomo.dungeonwalker.wsserver.core.actor.client.command.ClientCommand;

public interface EngineMessageMapper<O extends ClientCommand> {

    @NonNull
    O map(@NonNull EngineMessage message);

    boolean canMap(@NonNull EngineMessage message);

}

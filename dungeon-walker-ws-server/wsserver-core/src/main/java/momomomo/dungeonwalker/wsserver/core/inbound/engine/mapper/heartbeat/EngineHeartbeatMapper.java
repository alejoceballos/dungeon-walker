package momomomo.dungeonwalker.wsserver.core.inbound.engine.mapper.heartbeat;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import momomomo.dungeonwalker.contract.engine.EngineMessageProto.EngineMessage;
import momomomo.dungeonwalker.wsserver.core.actor.client.command.from.engine.EngineHeartbeatCommand;
import momomomo.dungeonwalker.wsserver.core.inbound.engine.mapper.EngineMessageMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EngineHeartbeatMapper implements EngineMessageMapper<EngineHeartbeatCommand> {

    @Override
    @NonNull
    public EngineHeartbeatCommand map(@NonNull final EngineMessage message) {
        return new EngineHeartbeatCommand();
    }

    @Override
    public boolean canMap(@NonNull final EngineMessage message) {
        return message.hasHeartbeat();
    }

}

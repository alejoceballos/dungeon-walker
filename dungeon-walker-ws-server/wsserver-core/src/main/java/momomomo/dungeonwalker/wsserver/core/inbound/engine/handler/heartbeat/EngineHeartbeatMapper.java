package momomomo.dungeonwalker.wsserver.core.inbound.engine.handler.heartbeat;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.contract.engine.EngineMessageProto.EngineMessage;
import momomomo.dungeonwalker.wsserver.core.actor.client.command.from.engine.EngineHeartbeatCommand;
import momomomo.dungeonwalker.wsserver.core.inbound.engine.handler.EngineMessageMapper;
import org.springframework.stereotype.Component;

@Slf4j
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

package momomomo.dungeonwalker.wsserver.core.inbound.engine.handler.dungeonstate;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.contract.engine.EngineMessageProto.EngineMessage;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.from.engine.BroadcastDungeonState;
import momomomo.dungeonwalker.wsserver.core.inbound.engine.handler.EngineInputMapper;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BroadcastDungeonStateMapper implements EngineInputMapper<BroadcastDungeonState> {

    @Override
    @NonNull
    public BroadcastDungeonState map(@NonNull final EngineMessage message) {
        return BroadcastDungeonState.of(message);
    }

}

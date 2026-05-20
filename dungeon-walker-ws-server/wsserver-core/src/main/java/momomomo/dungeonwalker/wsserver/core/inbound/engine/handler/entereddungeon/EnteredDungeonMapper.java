package momomomo.dungeonwalker.wsserver.core.inbound.engine.handler.entereddungeon;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.contract.engine.EngineMessageProto.EngineMessage;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.from.engine.EnteredTheDungeon;
import momomomo.dungeonwalker.wsserver.core.inbound.engine.handler.EngineInputMapper;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EnteredDungeonMapper implements EngineInputMapper<EnteredTheDungeon> {

    @Override
    @NonNull
    public EnteredTheDungeon map(@NonNull final EngineMessage message) {
        return EnteredTheDungeon.of(message);
    }

}

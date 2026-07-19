package momomomo.dungeonwalker.wsserver.core.inbound.engine.mapper.dungeoncellstate;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.contract.engine.EngineMessageProto.EngineMessage;
import momomomo.dungeonwalker.wsserver.core.actor.client.command.from.engine.DungeonCellStateChangedCommand;
import momomomo.dungeonwalker.wsserver.core.inbound.engine.mapper.EngineMessageMapper;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DungeonCellStateMapper implements EngineMessageMapper<DungeonCellStateChangedCommand> {

    @Override
    @NonNull
    public DungeonCellStateChangedCommand map(@NonNull final EngineMessage message) {
        return DungeonCellStateChangedCommand.of(message);
    }

    @Override
    public boolean canMap(@NonNull final EngineMessage message) {
        return message.hasDungeonCellState();
    }

}

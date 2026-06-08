package momomomo.dungeonwalker.wsserver.core.inbound.engine.mapper.dunageonstate;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.contract.engine.EngineMessageProto.EngineMessage;
import momomomo.dungeonwalker.wsserver.core.actor.client.command.from.engine.DungeonStateChangedCommand;
import momomomo.dungeonwalker.wsserver.core.inbound.engine.mapper.EngineMessageMapper;
import org.springframework.stereotype.Component;

import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

@Slf4j
@Component
@RequiredArgsConstructor
public class DungeonStateMapper implements EngineMessageMapper<DungeonStateChangedCommand> {

    @Override
    @NonNull
    public DungeonStateChangedCommand map(@NonNull final EngineMessage message) {
        return DungeonStateChangedCommand.of(message);
    }

    @Override
    public boolean canMap(@NonNull final EngineMessage message) {
        return message.hasDungeonState() &&
                message.getDungeonState().getHeight() > 0 &&
                message.getDungeonState().getWidth() > 0 &&
                isNotEmpty(message.getDungeonState().getCoordinatesMap());
    }

}

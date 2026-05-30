package momomomo.dungeonwalker.wsserver.core.inbound.engine.handler.entereddungeon;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.contract.engine.EngineMessageProto.EngineMessage;
import momomomo.dungeonwalker.wsserver.core.actor.client.command.from.engine.EnteredTheDungeonCommand;
import momomomo.dungeonwalker.wsserver.core.inbound.engine.handler.EngineMessageMapper;
import org.springframework.stereotype.Component;

import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

@Slf4j
@Component
@RequiredArgsConstructor
public class EnteredDungeonMapper implements EngineMessageMapper<EnteredTheDungeonCommand> {

    @Override
    @NonNull
    public EnteredTheDungeonCommand map(@NonNull final EngineMessage message) {
        return EnteredTheDungeonCommand.of(message);
    }

    @Override
    public boolean canMap(@NonNull final EngineMessage message) {
        return message.hasEnteredDungeon() &&
                message.getEnteredDungeon().getDungeonState().getHeight() > 0 &&
                message.getEnteredDungeon().getDungeonState().getWidth() > 0 &&
                isNotEmpty(message.getEnteredDungeon().getDungeonState().getCoordinatesMap());
    }

}

package momomomo.dungeonwalker.wsserver.core.inbound.engine.handler.entereddungeon;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.contract.engine.EngineMessageProto.EngineMessage;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.ConnectionCommand;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.from.engine.EnteredTheDungeon;
import momomomo.dungeonwalker.wsserver.core.inbound.engine.handler.EngineInputMapper;
import momomomo.dungeonwalker.wsserver.core.inbound.engine.handler.SelectableEngineInputHandler;
import org.apache.pekko.actor.typed.ActorRef;
import org.apache.pekko.actor.typed.pubsub.Topic;
import org.springframework.stereotype.Component;

import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Component
public class EnteredDungeonHandler extends SelectableEngineInputHandler {

    public EnteredDungeonHandler(
            final EngineInputMapper<EnteredTheDungeon> mapper,
            final ActorRef<Topic.Command<ConnectionCommand>> connectionBroadcastTopic
    ) {
        super(mapper, connectionBroadcastTopic);
    }

    @Override
    public boolean canHandle(@NonNull final EngineMessage message) {
        return message.hasEnteredDungeon() &&
                isNotBlank(message.getEnteredDungeon().getClientId()) &&
                isNotEmpty(message.getEnteredDungeon().getCoordinatesMap());
    }

}

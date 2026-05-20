package momomomo.dungeonwalker.wsserver.core.inbound.engine.handler.dungeonstate;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.contract.engine.EngineMessageProto.EngineMessage;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.ConnectionCommand;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.from.engine.BroadcastDungeonState;
import momomomo.dungeonwalker.wsserver.core.inbound.engine.handler.EngineInputMapper;
import momomomo.dungeonwalker.wsserver.core.inbound.engine.handler.SelectableEngineInputHandler;
import org.apache.pekko.actor.typed.ActorRef;
import org.apache.pekko.actor.typed.pubsub.Topic;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BroadcastDungeonStateHandler extends SelectableEngineInputHandler {

    public BroadcastDungeonStateHandler(
            final EngineInputMapper<BroadcastDungeonState> mapper,
            final ActorRef<Topic.Command<ConnectionCommand>> connectionBroadcastTopic
    ) {
        super(mapper, connectionBroadcastTopic);
    }

    @Override
    public boolean canHandle(@NonNull final EngineMessage message) {
        return message.hasDungeonState();
    }
}

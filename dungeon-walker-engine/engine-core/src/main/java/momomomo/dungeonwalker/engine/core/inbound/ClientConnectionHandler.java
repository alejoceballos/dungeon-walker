package momomomo.dungeonwalker.engine.core.inbound;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.contract.client.ClientRequestProto.ClientRequest;
import momomomo.dungeonwalker.engine.domain.DungeonMaster;
import momomomo.dungeonwalker.engine.domain.handler.MessageHandlerResult;
import momomomo.dungeonwalker.engine.domain.handler.SelectableHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClientConnectionHandler implements SelectableHandler<ClientRequest> {

    public final DungeonMaster dungeonMaster;

    @Override
    public MessageHandlerResult<ClientRequest> handle(@NonNull final ClientRequest message) {
        log.debug("---> [MESSAGE RECEIVER - Client] Message received: \"{}\"", message);
        dungeonMaster.enterTheDungeon(message.getClientId());
        return new ClientRequestResult(message);
    }

    @Override
    public boolean shouldHandle(@NonNull final ClientRequest message) {
        return message.hasConnection();
    }
}

package momomomo.dungeonwalker.engine.core.inbound;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.contract.client.ClientRequestProto.ClientRequest;
import momomomo.dungeonwalker.engine.domain.handler.MessageHandlerResult;
import momomomo.dungeonwalker.engine.domain.handler.SelectableHandler;
import momomomo.dungeonwalker.engine.domain.manager.PlayerManager;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClientConnectionHandler implements SelectableHandler<ClientRequest> {

    private static final String LABEL = "---> [CLIENT MSG HANDLER - Connection]";

    public final PlayerManager playerManager;

    @Override
    public MessageHandlerResult<ClientRequest> handle(@NonNull final ClientRequest message) {
        log.debug("---> [{} Message received: \"{}\"", LABEL, message);
        playerManager.enterTheDungeon(message.getClientId());
        return new ClientRequestResult(message);
    }

    @Override
    public boolean shouldHandle(@NonNull final ClientRequest message) {
        return message.hasConnection();
    }
}

package momomomo.dungeonwalker.engine.core.inbound;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.contract.client.ClientRequestProto.ClientRequest;
import momomomo.dungeonwalker.engine.domain.handler.MessageHandlerResult;
import momomomo.dungeonwalker.engine.domain.handler.SelectableHandler;
import momomomo.dungeonwalker.engine.domain.manager.PlayerManager;
import momomomo.dungeonwalker.engine.domain.model.walker.moving.Direction;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClientMovementHandler implements SelectableHandler<ClientRequest> {

    private static final String LABEL = "---> [CLIENT MSG HANDLER - Movement]";

    public final PlayerManager playerManager;

    @Override
    public MessageHandlerResult<ClientRequest> handle(@NonNull final ClientRequest message) {
        log.debug("---> [{} Message received: \"{}\"", LABEL, message);

        playerManager.move(
                message.getClientId(),
                Direction.of(message.getMovement().getDirection().name()));

        return new ClientRequestResult(message);
    }

    @Override
    public boolean shouldHandle(@NonNull final ClientRequest message) {
        return message.hasMovement();
    }
}

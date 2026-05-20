package momomomo.dungeonwalker.wsserver.core.inbound.client.handler.authenticate;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.wsserver.core.actor.ClusterShardingManager;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.from.client.AuthenticateFromClient;
import momomomo.dungeonwalker.wsserver.core.inbound.client.handler.ClientInputMapper;
import momomomo.dungeonwalker.wsserver.core.inbound.client.handler.SelectableClientInputHandler;
import momomomo.dungeonwalker.wsserver.domain.data.client.input.Authenticate;
import momomomo.dungeonwalker.wsserver.domain.data.client.input.InputData;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthenticateHandler extends SelectableClientInputHandler<Authenticate> {

    public AuthenticateHandler(
            final ClientInputMapper<Authenticate, AuthenticateFromClient> mapper,
            final ClusterShardingManager clusterShardingManager
    ) {
        super(mapper, clusterShardingManager);
    }

    @Override
    public boolean canHandle(@NonNull final InputData message) {
        return message instanceof Authenticate;
    }

}

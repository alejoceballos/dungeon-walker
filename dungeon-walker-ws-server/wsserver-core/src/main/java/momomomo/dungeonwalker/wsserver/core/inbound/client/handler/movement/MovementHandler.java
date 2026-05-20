package momomomo.dungeonwalker.wsserver.core.inbound.client.handler.movement;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.wsserver.core.actor.ClusterShardingManager;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.from.client.MovementFromClient;
import momomomo.dungeonwalker.wsserver.core.inbound.client.handler.ClientInputMapper;
import momomomo.dungeonwalker.wsserver.core.inbound.client.handler.SelectableClientInputHandler;
import momomomo.dungeonwalker.wsserver.domain.data.client.input.InputData;
import momomomo.dungeonwalker.wsserver.domain.data.client.input.Movement;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MovementHandler extends SelectableClientInputHandler<Movement> {

    public MovementHandler(
            final ClientInputMapper<Movement, MovementFromClient> mapper,
            final ClusterShardingManager clusterShardingManager
    ) {
        super(mapper, clusterShardingManager);
    }

    @Override
    public boolean canHandle(@NonNull final InputData message) {
        return message instanceof Movement;
    }

}

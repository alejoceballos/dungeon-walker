package momomomo.dungeonwalker.wsserver.core.inbound.client.handler.movement;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.from.client.MovementFromClient;
import momomomo.dungeonwalker.wsserver.core.inbound.client.handler.ClientInputMapper;
import momomomo.dungeonwalker.wsserver.domain.data.client.input.Movement;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MovementMapperClient implements ClientInputMapper<Movement, MovementFromClient> {

    @Override
    @NonNull
    public MovementFromClient map(@NonNull final Movement inputData) {
        return new MovementFromClient(inputData.direction());
    }

}

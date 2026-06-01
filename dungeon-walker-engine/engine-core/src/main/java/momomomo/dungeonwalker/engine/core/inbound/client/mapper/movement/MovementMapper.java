package momomomo.dungeonwalker.engine.core.inbound.client.mapper.movement;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.contract.client.ClientRequestProto.ClientRequest;
import momomomo.dungeonwalker.engine.core.actor.walker.command.from.client.Move;
import momomomo.dungeonwalker.engine.core.inbound.client.mapper.ClientRequestMapper;
import momomomo.dungeonwalker.engine.domain.model.walker.moving.Direction;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MovementMapper implements ClientRequestMapper<Move> {

    @Override
    public @NonNull Move map(@NonNull final ClientRequest request) {
        return new Move(Direction.of(request.getMovement().getDirection().name()));
    }

    @Override
    public boolean canMap(@NonNull final ClientRequest request) {
        return request.hasMovement();
    }

}

package momomomo.dungeonwalker.wsserver.core.mapper;

import jakarta.annotation.Nonnull;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.contract.client.ClientRequestProto.ClientRequest;
import momomomo.dungeonwalker.contract.client.DirectionProto.Direction;
import momomomo.dungeonwalker.contract.client.MovementProto;
import momomomo.dungeonwalker.wsserver.domain.input.Movement;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MovementMapper implements InputDataMapper<Movement, ClientRequest> {

    @Nonnull
    @Override
    public ClientRequest map(@NonNull final String clientId, @NonNull final Movement inputData) {
        log.debug("---> [MAPPER - Identity] mapping \"{}\"", inputData);

        final var direction = switch (inputData.direction()) {
            case E -> Direction.E;
            case W -> Direction.W;
            case N -> Direction.N;
            case S -> Direction.S;
            case NE -> Direction.NE;
            case NW -> Direction.NW;
            case SE -> Direction.SE;
            case SW -> Direction.SW;
        };

        return ClientRequest.newBuilder()
                .setClientId(clientId)
                .setMovement(MovementProto.Movement.newBuilder()
                        .setDirection(direction)
                        .build())
                .build();
    }

}

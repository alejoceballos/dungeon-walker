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

    private static final String LABEL = "---> [MAPPER - Movement]";

    @Override
    @Nonnull
    public ClientRequest map(@NonNull final Movement inputData) {
        log.debug("{} mapping \"{}\"", LABEL, inputData);

        return ClientRequest
                .newBuilder()
                .setClientId(inputData.clientId())
                .setMovement(MovementProto.Movement.newBuilder()
                        .setDirection(Direction.valueOf(inputData.direction().name()))
                        .build())
                .build();
    }

}

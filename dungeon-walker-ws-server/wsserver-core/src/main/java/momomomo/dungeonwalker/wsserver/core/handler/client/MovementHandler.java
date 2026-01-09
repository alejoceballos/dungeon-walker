package momomomo.dungeonwalker.wsserver.core.handler.client;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.contract.client.ClientRequestProto.ClientRequest;
import momomomo.dungeonwalker.wsserver.core.mapper.InputDataMapper;
import momomomo.dungeonwalker.wsserver.core.validator.InputDataValidator;
import momomomo.dungeonwalker.wsserver.domain.input.InputData;
import momomomo.dungeonwalker.wsserver.domain.input.Movement;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MovementHandler extends SelectableInputDataHandler<Movement> {

    public MovementHandler(
            final InputDataMapper<Movement, ClientRequest> mapper,
            final InputDataValidator<Movement> validator
    ) {
        super(mapper, validator);
    }

    @Override
    public boolean canHandle(@NonNull InputData message) {
        return message instanceof Movement;
    }

}

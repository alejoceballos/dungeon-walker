package momomomo.dungeonwalker.wsserver.core.handler.client;

import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.contract.client.ClientRequestProto.ClientRequest;
import momomomo.dungeonwalker.wsserver.core.mapper.InputDataMapper;
import momomomo.dungeonwalker.wsserver.core.validator.InputDataValidator;
import momomomo.dungeonwalker.wsserver.domain.input.InputData;
import momomomo.dungeonwalker.wsserver.domain.input.Movement;
import momomomo.dungeonwalker.wsserver.domain.outbound.Sender;
import org.springframework.stereotype.Component;

import static java.util.Objects.nonNull;

@Slf4j
@Component
public class MovementHandler extends SelectableInputDataHandler<Movement, ClientRequest> {

    public MovementHandler(
            final InputDataMapper<Movement, ClientRequest> mapper,
            final InputDataValidator<Movement> validator,
            final Sender<ClientRequest> sender) {
        super(mapper, validator, sender);
    }

    @Override
    public boolean isSelected(final InputData data) {
        return nonNull(data) && data instanceof Movement;
    }

}

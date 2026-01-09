package momomomo.dungeonwalker.wsserver.core.handler.client;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.contract.client.ClientRequestProto.ClientRequest;
import momomomo.dungeonwalker.wsserver.core.mapper.InputDataMapper;
import momomomo.dungeonwalker.wsserver.core.validator.InputDataValidator;
import momomomo.dungeonwalker.wsserver.domain.input.Identity;
import momomomo.dungeonwalker.wsserver.domain.input.InputData;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class IdentityHandler extends SelectableInputDataHandler<Identity> {

    public IdentityHandler(
            final InputDataMapper<Identity, ClientRequest> mapper,
            final InputDataValidator<Identity> validator
    ) {
        super(mapper, validator);
    }

    @Override
    public boolean canHandle(@NonNull InputData message) {
        return message instanceof Identity;
    }

}

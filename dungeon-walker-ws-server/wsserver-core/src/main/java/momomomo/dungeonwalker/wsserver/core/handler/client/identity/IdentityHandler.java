package momomomo.dungeonwalker.wsserver.core.handler.client.identity;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.contract.client.ClientRequestProto.ClientRequest;
import momomomo.dungeonwalker.wsserver.core.handler.client.SelectableInputDataHandler;
import momomomo.dungeonwalker.wsserver.core.handler.client.InputDataMapper;
import momomomo.dungeonwalker.wsserver.core.handler.client.InputDataValidator;
import momomomo.dungeonwalker.wsserver.domain.input.client.Identity;
import momomomo.dungeonwalker.wsserver.domain.input.client.InputData;
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
    public boolean canHandle(@NonNull final InputData message) {
        return message instanceof Identity;
    }

}

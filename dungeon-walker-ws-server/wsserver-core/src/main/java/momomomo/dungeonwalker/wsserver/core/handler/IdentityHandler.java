package momomomo.dungeonwalker.wsserver.core.handler;

import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.clientrequest.AddClientWalkerProto;
import momomomo.dungeonwalker.wsserver.core.mapper.InputDataMapper;
import momomomo.dungeonwalker.wsserver.core.validator.InputDataValidator;
import momomomo.dungeonwalker.wsserver.domain.input.Identity;
import momomomo.dungeonwalker.wsserver.domain.input.InputData;
import momomomo.dungeonwalker.wsserver.domain.outbound.Sender;
import org.springframework.stereotype.Component;

import static java.util.Objects.nonNull;

@Slf4j
@Component
public class IdentityHandler extends SelectableInputDataHandler<Identity, AddClientWalkerProto.AddClientWalker> {

    public IdentityHandler(
            final InputDataMapper<Identity, AddClientWalkerProto.AddClientWalker> mapper,
            final InputDataValidator<Identity> validator,
            final Sender<AddClientWalkerProto.AddClientWalker> sender) {
        super(mapper, validator, sender);
    }

    @Override
    public boolean isSelected(final InputData data) {
        return nonNull(data) && data instanceof Identity;
    }

}

package momomomo.dungeonwalker.wsserver.core.handler.client;

import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.contract.client.ClientRequestProto.ClientRequest;
import momomomo.dungeonwalker.wsserver.core.mapper.InputDataMapper;
import momomomo.dungeonwalker.wsserver.core.validator.InputDataValidator;
import momomomo.dungeonwalker.wsserver.domain.input.ClientHeartbeat;
import momomomo.dungeonwalker.wsserver.domain.input.InputData;
import momomomo.dungeonwalker.wsserver.domain.outbound.Sender;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;
import static momomomo.dungeonwalker.wsserver.core.handler.client.HandlingResult.Type.SUCCESS;

@Slf4j
@Component
public class HeartbeatHandler extends SelectableInputDataHandler<ClientHeartbeat, ClientRequest> {

    public HeartbeatHandler(
            final InputDataMapper<ClientHeartbeat, ClientRequest> mapper,
            final InputDataValidator<ClientHeartbeat> validator,
            final Sender<ClientRequest> sender) {
        super(mapper, validator, sender);
    }

    @Override
    public @NonNull CompletableFuture<HandlingResult> handle(@lombok.NonNull String clientId, @NonNull ClientHeartbeat inputData) {
        return CompletableFuture.completedFuture(
                HandlingResult.builder()
                        .type(SUCCESS)
                        .errors(emptyList())
                        .build());
    }

    @Override
    public boolean isSelected(final InputData data) {
        return nonNull(data) && data instanceof ClientHeartbeat;
    }

}

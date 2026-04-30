package momomomo.dungeonwalker.wsserver.core.handler.client.heartbeat;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.contract.client.ClientRequestProto.ClientRequest;
import momomomo.dungeonwalker.wsserver.core.handler.client.HandlingResult;
import momomomo.dungeonwalker.wsserver.core.handler.client.InputDataMapper;
import momomomo.dungeonwalker.wsserver.core.handler.client.InputDataValidator;
import momomomo.dungeonwalker.wsserver.core.handler.client.SelectableInputDataHandler;
import momomomo.dungeonwalker.wsserver.domain.input.client.ClientHeartbeat;
import momomomo.dungeonwalker.wsserver.domain.input.client.InputData;
import momomomo.dungeonwalker.wsserver.domain.outbound.Sender;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

import static java.util.Collections.emptyList;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static momomomo.dungeonwalker.wsserver.core.handler.client.HandlingResult.Type.SUCCESS;

@Slf4j
@Component
public class HeartbeatHandler extends SelectableInputDataHandler<ClientHeartbeat> {

    private static final String LABEL = "---> [DATA HANDLER -";

    public HeartbeatHandler(
            final InputDataMapper<ClientHeartbeat, ClientRequest> mapper,
            final InputDataValidator<ClientHeartbeat> validator
    ) {
        super(mapper, validator);
    }

    @Override
    public @NonNull CompletableFuture<HandlingResult> handle(
            @NonNull final ClientHeartbeat inputData,
            @NonNull final Sender<ClientRequest> unused
    ) {
        log.debug("{} {}] Data: {}", LABEL, this.getClass().getSimpleName(), inputData);

        return completedFuture(
                HandlingResult
                        .builder()
                        .type(SUCCESS)
                        .errors(emptyList())
                        .build());
    }

    @Override
    public boolean canHandle(@NonNull final InputData message) {
        return message instanceof ClientHeartbeat;
    }

}

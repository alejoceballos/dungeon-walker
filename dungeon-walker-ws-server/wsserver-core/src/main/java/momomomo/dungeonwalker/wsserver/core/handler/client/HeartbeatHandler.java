package momomomo.dungeonwalker.wsserver.core.handler.client;

import jakarta.annotation.Nonnull;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.contract.client.ClientRequestProto.ClientRequest;
import momomomo.dungeonwalker.wsserver.core.mapper.InputDataMapper;
import momomomo.dungeonwalker.wsserver.core.validator.InputDataValidator;
import momomomo.dungeonwalker.wsserver.domain.input.ClientHeartbeat;
import momomomo.dungeonwalker.wsserver.domain.input.InputData;
import momomomo.dungeonwalker.wsserver.domain.outbound.Sender;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

import static java.util.Collections.emptyList;
import static momomomo.dungeonwalker.wsserver.core.handler.client.HandlingResult.Type.SUCCESS;

@Slf4j
@Component
public class HeartbeatHandler extends SelectableInputDataHandler<ClientHeartbeat> {

    public HeartbeatHandler(
            final InputDataMapper<ClientHeartbeat, ClientRequest> mapper,
            final InputDataValidator<ClientHeartbeat> validator
    ) {
        super(mapper, validator);
    }

    @Override
    public @Nonnull CompletableFuture<HandlingResult> handle(
            @NonNull final ClientHeartbeat inputData,
            @NonNull final Sender<ClientRequest> unused
    ) {
        return CompletableFuture.completedFuture(
                HandlingResult.builder()
                        .type(SUCCESS)
                        .errors(emptyList())
                        .build());
    }

    @Override
    public boolean canHandle(@NonNull InputData message) {
        return message instanceof ClientHeartbeat;
    }

}

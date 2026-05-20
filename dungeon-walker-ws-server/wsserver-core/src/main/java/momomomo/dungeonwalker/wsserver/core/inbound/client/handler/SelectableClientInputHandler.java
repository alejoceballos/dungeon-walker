package momomomo.dungeonwalker.wsserver.core.inbound.client.handler;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.wsserver.core.actor.ClusterShardingManager;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.ConnectionCommand;
import momomomo.dungeonwalker.wsserver.domain.data.client.input.InputData;
import momomomo.dungeonwalker.wsserver.domain.handler.HandlingResult;
import momomomo.dungeonwalker.wsserver.domain.handler.MessageHandler;

import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.completedFuture;

@Slf4j
@RequiredArgsConstructor
public abstract class SelectableClientInputHandler<I extends InputData>
        implements MessageHandler<HandlerContext, CompletableFuture<HandlingResult>> {

    private static final String LABEL = "---> [DATA HANDLER -";

    private final ClientInputMapper<I, ? extends ConnectionCommand> mapper;
    private final ClusterShardingManager clusterShardingManager;


    @SuppressWarnings("unchecked")
    @Override
    @NonNull
    public CompletableFuture<HandlingResult> handle(@NonNull final HandlerContext context) {
        log.debug("{} {}] Data: {}", LABEL, this.getClass().getSimpleName(), context.data());

        clusterShardingManager
                .getConnectionEntityRef(context.clientOutboundId())
                .tell(mapper.map((I) context.data()));

        return completedFuture(HandlingResult.success());
    }

    protected abstract boolean canHandle(@NonNull InputData message);

}

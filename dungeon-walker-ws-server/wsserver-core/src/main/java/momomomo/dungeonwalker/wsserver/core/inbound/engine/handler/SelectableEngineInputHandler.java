package momomomo.dungeonwalker.wsserver.core.inbound.engine.handler;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.contract.engine.EngineMessageProto.EngineMessage;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.ConnectionCommand;
import momomomo.dungeonwalker.wsserver.domain.handler.HandlingResult;
import momomomo.dungeonwalker.wsserver.domain.handler.MessageHandler;
import org.apache.pekko.actor.typed.ActorRef;
import org.apache.pekko.actor.typed.pubsub.Topic;

import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.completedFuture;

@Slf4j
@RequiredArgsConstructor
public abstract class SelectableEngineInputHandler
        implements MessageHandler<EngineMessage, CompletableFuture<HandlingResult>> {

    private static final String LABEL = "---> [DATA HANDLER -";

    private final EngineInputMapper<? extends ConnectionCommand> mapper;
    private final ActorRef<Topic.Command<ConnectionCommand>> connectionBroadcastTopic;


    @Override
    @NonNull
    public CompletableFuture<HandlingResult> handle(@NonNull final EngineMessage message) {
        log.debug("{} {}] Data: {}", LABEL, this.getClass().getSimpleName(), message);

        connectionBroadcastTopic.tell(Topic.publish(mapper.map(message)));
        return completedFuture(HandlingResult.success());
    }

    protected abstract boolean canHandle(@NonNull EngineMessage message);

}

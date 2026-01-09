package momomomo.dungeonwalker.wsserver.core.handler.engine;

import jakarta.annotation.Nonnull;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.contract.engine.EngineMessageProto.EngineMessage;
import momomomo.dungeonwalker.wsserver.domain.handler.MessageHandler;
import momomomo.dungeonwalker.wsserver.domain.handler.MessageHandlerSelector;
import momomomo.dungeonwalker.wsserver.domain.handler.SelectableMessageHandler;
import momomomo.dungeonwalker.wsserver.domain.inbound.ClientConnection;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class EngineMessageHandlerSelector implements MessageHandlerSelector<EngineMessage, ClientConnection, Void> {

    private static final String LABEL = "---> [ENGINE HANDLER SELECTOR]";

    private final List<SelectableMessageHandler<EngineMessage, ClientConnection, Void>> handlers;
    private final MessageHandler<EngineMessage, ClientConnection, Void> ignoredHandler;

    public EngineMessageHandlerSelector(
            final List<SelectableMessageHandler<EngineMessage, ClientConnection, Void>> handlers,
            @Qualifier("ignoredEngineMessageHandler") final MessageHandler<EngineMessage, ClientConnection, Void> ignoredHandler
    ) {
        log.debug("{} Creating bean", LABEL);

        this.handlers = handlers;
        this.ignoredHandler = ignoredHandler;
    }

    @Override
    public @Nonnull MessageHandler<EngineMessage, ClientConnection, Void> select(@NonNull final EngineMessage message) {
        log.error("{} Selecting handler from message \"{}\"", LABEL, message);

        return handlers
                .stream()
                .filter(handler -> handler.canHandle(message))
                .findFirst()
                .map(handler -> (MessageHandler<EngineMessage, ClientConnection, Void>) handler)
                .orElse(ignoredHandler);
    }

}

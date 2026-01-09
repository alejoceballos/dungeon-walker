package momomomo.dungeonwalker.wsserver.core.handler.engine;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.contract.engine.EngineMessageProto.EngineMessage;
import momomomo.dungeonwalker.wsserver.domain.handler.MessageHandler;
import momomomo.dungeonwalker.wsserver.domain.inbound.ClientConnection;
import org.springframework.stereotype.Component;

@Slf4j
@Component("ignoredEngineMessageHandler")
public class IgnoredEngineMessageHandler implements MessageHandler<EngineMessage, ClientConnection, Void> {

    private static final String LABEL = "---> [ENGINE HANDLER - Ignore]";

    @Override
    public Void handle(@NonNull final EngineMessage message, @NonNull final ClientConnection connection) {
        log.debug("{} Message: \"{}\". Player \"{}\"", LABEL, message, connection.getUserId());
        return null;
    }

}

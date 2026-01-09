package momomomo.dungeonwalker.wsserver.core.handler.engine;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.contract.engine.EngineMessageProto.EngineMessage;
import momomomo.dungeonwalker.wsserver.domain.handler.SelectableMessageHandler;
import momomomo.dungeonwalker.wsserver.domain.inbound.ClientConnection;
import momomomo.dungeonwalker.wsserver.domain.output.EngineCoordinates;
import momomomo.dungeonwalker.wsserver.domain.output.EngineWalkersCoordinates;
import momomomo.dungeonwalker.wsserver.domain.output.Output;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class BroadcastWalkersPositionsHandler implements SelectableMessageHandler<EngineMessage, ClientConnection, Void> {

    private static final String LABEL = "---> [ENGINE HANDLER - Broadcast Walkers Positions]";

    @Override
    public Void handle(@NonNull final EngineMessage message, @NonNull final ClientConnection connection) {
        log.debug("{} Message: \"{}\". Player \"{}\"", LABEL, message, connection.getUserId());

        final var transformedMap = message
                .getWalkerPositions()
                .getCoordinatesByWalkerIdMap()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> new EngineCoordinates(
                                entry.getValue().getX(),
                                entry.getValue().getY())));

        connection.send(Output.of(new EngineWalkersCoordinates(transformedMap)));

        return null;
    }

    @Override
    public boolean canHandle(@NonNull EngineMessage message) {
        return message.hasWalkerPositions();
    }
}

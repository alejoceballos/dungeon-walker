package momomomo.dungeonwalker.engine.core.service;

import lombok.RequiredArgsConstructor;
import momomomo.dungeonwalker.contract.engine.CoordinatesProto;
import momomomo.dungeonwalker.contract.engine.EngineMessageProto.EngineMessage;
import momomomo.dungeonwalker.contract.engine.WalkersPositionsProto.WalkersPositions;
import momomomo.dungeonwalker.engine.domain.model.coordinates.Coordinates;
import momomomo.dungeonwalker.engine.domain.outbound.Sender;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageSender {

    private final Sender<EngineMessage> sender;

    public void broadcast(final Map<String, Coordinates> walkersPositions) {
        final var protoWalkersPositions = walkersPositions
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> CoordinatesProto.Coordinates
                                .newBuilder()
                                .setX(entry.getValue().x())
                                .setY(entry.getValue().y())
                                .build()));

        final var message = EngineMessage.newBuilder()
                .setWalkerPositions(
                        WalkersPositions
                                .newBuilder()
                                .putAllCoordinatesByWalkerId(protoWalkersPositions)
                                .build())
                .build();

        sender.send(message);
    }
}

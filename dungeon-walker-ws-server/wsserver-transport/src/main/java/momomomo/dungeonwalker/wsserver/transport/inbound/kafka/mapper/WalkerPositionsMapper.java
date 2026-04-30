package momomomo.dungeonwalker.wsserver.transport.inbound.kafka.mapper;

import momomomo.dungeonwalker.contract.engine.EngineMessageProto.EngineMessage;
import momomomo.dungeonwalker.wsserver.domain.input.engine.CoordinatesData;
import momomomo.dungeonwalker.wsserver.domain.input.engine.WalkersPositionsData;
import momomomo.dungeonwalker.wsserver.transport.inbound.kafka.EngineMessageMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static momomomo.dungeonwalker.contract.engine.EngineMessageProto.EngineMessage.DataCase.WALKERPOSITIONS;

@Component
public class WalkerPositionsMapper implements EngineMessageMapper<WalkersPositionsData> {

    @Override
    public WalkersPositionsData map(final ConsumerRecord<String, EngineMessage> consumerRecord) {
        final var value = consumerRecord.value();

        if (isNull(value) || value.getDataCase() != WALKERPOSITIONS || !value.hasWalkerPositions()) {
            return null;
        }

        final var walkersPositions = value
                .getWalkerPositions()
                .getCoordinatesByWalkerIdMap()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> new CoordinatesData(
                                entry.getValue().getX(),
                                entry.getValue().getY())));

        return new WalkersPositionsData(walkersPositions);
    }

}

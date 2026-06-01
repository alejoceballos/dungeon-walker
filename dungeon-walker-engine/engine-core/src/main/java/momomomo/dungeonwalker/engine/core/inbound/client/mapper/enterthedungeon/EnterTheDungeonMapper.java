package momomomo.dungeonwalker.engine.core.inbound.client.mapper.enterthedungeon;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import momomomo.dungeonwalker.contract.client.ClientRequestProto.ClientRequest;
import momomomo.dungeonwalker.engine.core.actor.walker.command.from.client.WakeUp;
import momomomo.dungeonwalker.engine.core.inbound.client.mapper.ClientRequestMapper;
import momomomo.dungeonwalker.engine.domain.model.dungeon.placing.DungeonPlacingStrategy;
import momomomo.dungeonwalker.engine.domain.model.dungeon.placing.SpiralStrategy;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EnterTheDungeonMapper implements ClientRequestMapper<WakeUp> {

    // When having several dungeon levels, the placing strategy will depend on the level
    private static final DungeonPlacingStrategy PLACING_STRATEGY = new SpiralStrategy();

    @Override
    public @NonNull WakeUp map(@NonNull final ClientRequest request) {
        return new WakeUp(PLACING_STRATEGY);
    }

    @Override
    public boolean canMap(@NonNull final ClientRequest message) {
        return message.hasEnterDungeon();
    }

}

package momomomo.dungeonwalker.engine.core.inbound.client.mapper.leave;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.contract.client.ClientRequestProto.ClientRequest;
import momomomo.dungeonwalker.engine.core.actor.walker.command.from.client.Leave;
import momomomo.dungeonwalker.engine.core.inbound.client.mapper.ClientRequestMapper;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LeaveHandler implements ClientRequestMapper<Leave> {

    @Override
    public @NonNull Leave map(@NonNull final ClientRequest request) {
        return new Leave();
    }

    @Override
    public boolean canMap(@NonNull final ClientRequest request) {
        return request.hasLeaveDungeon();
    }

}

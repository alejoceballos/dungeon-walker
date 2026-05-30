package momomomo.dungeonwalker.wsserver.core.inbound.user.mapper.heartbeat;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.from.user.UserHeartbeatCommand;
import momomomo.dungeonwalker.wsserver.core.inbound.user.mapper.UserInputMapper;
import momomomo.dungeonwalker.wsserver.domain.data.user.input.InputData;
import momomomo.dungeonwalker.wsserver.domain.data.user.input.UserHeartbeat;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HeartbeatMapper implements UserInputMapper<UserHeartbeat, UserHeartbeatCommand> {

    @Override
    public @NonNull UserHeartbeatCommand map(@NonNull final UserHeartbeat input) {
        return new UserHeartbeatCommand();
    }

    @Override
    public boolean canMap(@NonNull final InputData input) {
        return input instanceof UserHeartbeat;
    }

}

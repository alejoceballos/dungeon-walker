package momomomo.dungeonwalker.wsserver.core.inbound.user.mapper.movement;

import lombok.NonNull;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.from.user.UserMoveCommand;
import momomomo.dungeonwalker.wsserver.core.inbound.user.mapper.UserInputMapper;
import momomomo.dungeonwalker.wsserver.domain.data.user.input.InputData;
import momomomo.dungeonwalker.wsserver.domain.data.user.input.UserMovement;
import org.springframework.stereotype.Component;

@Component
public class MovementMapper implements UserInputMapper<UserMovement, UserMoveCommand> {

    @Override
    public @NonNull UserMoveCommand map(@NonNull final UserMovement inputData) {
        return new UserMoveCommand(inputData.direction());
    }

    @Override
    public boolean canMap(@NonNull final InputData input) {
        return input instanceof UserMovement;
    }

}


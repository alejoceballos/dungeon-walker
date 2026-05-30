package momomomo.dungeonwalker.wsserver.core.inbound.user.mapper.authentication;

import lombok.NonNull;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.from.user.UserAuthenticateCommand;
import momomomo.dungeonwalker.wsserver.core.inbound.user.mapper.UserInputMapper;
import momomomo.dungeonwalker.wsserver.domain.data.user.input.InputData;
import momomomo.dungeonwalker.wsserver.domain.data.user.input.UserAuthentication;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationMapper implements UserInputMapper<UserAuthentication, UserAuthenticateCommand> {

    @Override
    public @NonNull UserAuthenticateCommand map(@NonNull final UserAuthentication input) {
        return new UserAuthenticateCommand(input.token());
    }

    @Override
    public boolean canMap(@NonNull final InputData input) {
        return input instanceof UserAuthentication;
    }
}

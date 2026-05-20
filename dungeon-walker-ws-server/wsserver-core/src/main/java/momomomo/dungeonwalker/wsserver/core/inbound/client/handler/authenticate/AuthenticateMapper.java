package momomomo.dungeonwalker.wsserver.core.inbound.client.handler.authenticate;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.from.client.AuthenticateFromClient;
import momomomo.dungeonwalker.wsserver.core.inbound.client.handler.ClientInputMapper;
import momomomo.dungeonwalker.wsserver.domain.data.client.input.Authenticate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticateMapper implements ClientInputMapper<Authenticate, AuthenticateFromClient> {

    @Override
    @NonNull
    public AuthenticateFromClient map(@NonNull final Authenticate inputData) {
        return new AuthenticateFromClient(inputData.credentials());
    }

}

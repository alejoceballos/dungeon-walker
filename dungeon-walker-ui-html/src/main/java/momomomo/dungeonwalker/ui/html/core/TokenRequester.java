package momomomo.dungeonwalker.ui.html.core;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.ui.html.domain.outbound.security.SecurityGateway;
import momomomo.dungeonwalker.ui.html.domain.service.SecurityService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenRequester implements SecurityService {

    private final SecurityGateway gateway;

    @Override
    public String requestToken(@NonNull final String userName, @NonNull final String password) {
        log.debug("Retrieving token for user: {}/{}", userName, password);

        final var token = gateway.requestToken(userName, password).accessToken();

        log.debug("Token retrieved for user: {}/{} = {}", userName, password, token);

        return token;
    }
}

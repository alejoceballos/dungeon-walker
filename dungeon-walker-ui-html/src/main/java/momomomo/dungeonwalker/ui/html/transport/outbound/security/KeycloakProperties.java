package momomomo.dungeonwalker.ui.html.transport.outbound.security;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KeycloakProperties {

    @Value("${keycloak.token.client-id}")
    @Getter
    private String clientId;

    @Value("${keycloak.token.protocol}")
    @Getter
    private String protocol;

    @Value("${keycloak.token.host-port}")
    @Getter
    private String hostPort;

    @Value("${keycloak.token.path}")
    @Getter
    private String path;

}

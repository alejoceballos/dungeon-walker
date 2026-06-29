package momomomo.dungeonwalker.ui.html.transport.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ViewProperties {

    public ViewProperties(
            @Value("${view.messages.max-count}") final int messagesMaxCount,
            @Value("${view.security.protocol}") final String securityProtocol,
            @Value("${view.security.host}") final String securityHost,
            @Value("${view.security.endpoint}") final String securityEndpoint,
            @Value("${view.web-socket.protocol}") final String webSocketProtocol,
            @Value("${view.web-socket.host}") final String webSocketHost,
            @Value("${view.web-socket.endpoint}") final String webSocketEndpoint
    ) {
        this.messagesMaxCount = messagesMaxCount;
        this.securityProtocol = securityProtocol;
        this.securityHost = securityHost;
        this.securityEndpoint = securityEndpoint;
        this.webSocketProtocol = webSocketProtocol;
        this.webSocketHost = webSocketHost;
        this.webSocketEndpoint = webSocketEndpoint;
    }

    @Getter
    private final int messagesMaxCount;

    @Getter
    private final String securityProtocol;

    @Getter
    private final String securityHost;

    @Getter
    private final String securityEndpoint;

    @Getter
    private final String webSocketProtocol;

    @Getter
    private final String webSocketHost;

    @Getter
    private final String webSocketEndpoint;

}

package momomomo.dungeonwalker.wsserver.transport.config;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.wsserver.transport.inbound.WsHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final WsHandler wsHandler;
    private final WebSocketProps webSocketProps;

    @Override
    public void registerWebSocketHandlers(@NonNull final WebSocketHandlerRegistry registry) {
        log.info("---> [WEB SOCKET - Config] WebSocket handler registered");
        registry.addHandler(
                        wsHandler,
                        webSocketProps.getEndpoint())
                .setAllowedOriginPatterns(
                        webSocketProps.getAllowedOriginPatterns());
    }

}

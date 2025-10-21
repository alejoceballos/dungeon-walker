package momomomo.dungeonwalker.wsserver.transport.inbound;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.wsserver.domain.connection.ConnectionManager;
import momomomo.dungeonwalker.wsserver.domain.input.Input;
import momomomo.dungeonwalker.wsserver.transport.connection.WebSocketSessionAdapter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@Component
@RequiredArgsConstructor
public class WsHandler extends TextWebSocketHandler {

    private final ConnectionManager connectionManager;
    private final ObjectMapper jsonMapper;

    @PostConstruct
    public void init() {
        log.info("---> [WS Server - Handler] Bean created");
    }

    @Override
    public void afterConnectionEstablished(@NonNull final WebSocketSession session) {
        log.info("---> [WS Server - Handler] Connection established with session \"{}\"", session.getId());

        connectionManager.establish(new WebSocketSessionAdapter(session, jsonMapper));
    }

    @Override
    public void afterConnectionClosed(@NonNull final WebSocketSession session, @NonNull final CloseStatus status) {
        log.info("---> [WS Server - Handler] Connection closed for session \"{}\"", session.getId());

        connectionManager.close(new WebSocketSessionAdapter(session, jsonMapper));
    }

    @Override
    protected void handleTextMessage(@NonNull final WebSocketSession session, @NonNull final TextMessage message) {
        log.info("---> [WS Server - Handler] Session \"{}\" received the message \"{}\"",
                session.getId(),
                message.getPayload());

        try {
            final var input = jsonMapper.readValue(message.getPayload(), Input.class);
            connectionManager.handleMessage(new WebSocketSessionAdapter(session, jsonMapper), input);

        } catch (final JsonProcessingException e) {
            log.error("---> [WS Server - Handler] Error parsing message \"{}\"", message.getPayload(), e);
        }

    }

}

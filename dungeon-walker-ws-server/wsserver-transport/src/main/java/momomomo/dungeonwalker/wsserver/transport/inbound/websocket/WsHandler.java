package momomomo.dungeonwalker.wsserver.transport.inbound.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.wsserver.domain.inbound.ConnectionManager;
import momomomo.dungeonwalker.wsserver.domain.input.client.Input;
import momomomo.dungeonwalker.wsserver.transport.connection.WebSocketSessionAdapter;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@Component
@RequiredArgsConstructor
public class WsHandler extends TextWebSocketHandler {

    private static final String LABEL = "---> [WS Server - Handler]";

    private final ConnectionManager connectionManager;
    private final ObjectMapper jsonMapper;

    @PostConstruct
    public void init() {
        log.info("{} Bean created", LABEL);
    }

    @Override
    public void afterConnectionEstablished(@NonNull final WebSocketSession session) {
        log.info("{} Connection established with session \"{}\"", LABEL, session.getId());

        connectionManager.establish(new WebSocketSessionAdapter(session, jsonMapper));
    }

    @Override
    public void afterConnectionClosed(
            @NonNull final WebSocketSession session,
            @NonNull final CloseStatus status
    ) {
        log.info("{} Connection closed for session \"{}\"", LABEL, session.getId());

        connectionManager.close(new WebSocketSessionAdapter(session, jsonMapper));
    }

    @Override
    protected void handleTextMessage(
            @NonNull final WebSocketSession session,
            @NonNull final TextMessage message
    ) {
        log.info("{} Session \"{}\" received the message \"{}\"", LABEL, session.getId(), message.getPayload());

        try {
            final var input = jsonMapper.readValue(message.getPayload(), Input.class);
            connectionManager.handleMessage(new WebSocketSessionAdapter(session, jsonMapper), input);

        } catch (final JsonProcessingException e) {
            log.error("{} Error parsing message \"{}\"", LABEL, message.getPayload(), e);
        }
    }

}

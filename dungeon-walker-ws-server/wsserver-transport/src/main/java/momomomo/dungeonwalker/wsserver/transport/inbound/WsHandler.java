package momomomo.dungeonwalker.wsserver.transport.inbound;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.wsserver.domain.data.user.input.Input;
import momomomo.dungeonwalker.wsserver.domain.data.user.output.ClientErrors;
import momomomo.dungeonwalker.wsserver.domain.data.user.output.Output;
import momomomo.dungeonwalker.wsserver.domain.data.user.output.ServerErrors;
import momomomo.dungeonwalker.wsserver.domain.inbound.UserInbound;
import momomomo.dungeonwalker.wsserver.domain.outbound.UserConnection;
import momomomo.dungeonwalker.wsserver.transport.connection.WebSocketSessionAdapter;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class WsHandler extends TextWebSocketHandler {

    private static final String LABEL = "---> [WS Server - Handler]";

    private final UserInbound userInbound;
    private final ObjectMapper jsonMapper;

    @PostConstruct
    public void init() {
        log.info("{} Bean created", LABEL);
    }

    @Override
    public void afterConnectionEstablished(@NonNull final WebSocketSession session) {
        log.info("{} Connection established with session \"{}\"", LABEL, session.getId());
        userInbound.establish(createClientInbound(session));
    }

    @Override
    public void afterConnectionClosed(@NonNull final WebSocketSession session, @NonNull final CloseStatus status) {
        log.info("{} Connection closed for session \"{}\". Status: {}", LABEL, session.getId(), status);
        userInbound.close(createClientInbound(session));
    }

    @Override
    protected void handleTextMessage(
            @NonNull final WebSocketSession session,
            @NonNull final TextMessage message
    ) throws IOException {
        final var payload = message.getPayload();

        log.debug("{} Session \"{}\" received the message \"{}\"", LABEL, session.getId(), payload);

        try {
            final var input = jsonMapper.readValue(payload, Input.class);
            userInbound.handle(createClientInbound(session), input);

        } catch (final JsonProcessingException e) {
            log.error("{} Error parsing message \"{}\"", LABEL, payload, e);
            final var output = Output.of(new ClientErrors(List.of("Invalid message: %" + payload)));
            session.sendMessage(new TextMessage(jsonMapper.writeValueAsString(output)));

        } catch (final Exception e) {
            log.error("{} Unexpected error processing message \"{}\"", LABEL, payload, e);
            final var output = Output.of(new ServerErrors(List.of("An unexpected error occurred while processing the message")));
            session.sendMessage(new TextMessage(jsonMapper.writeValueAsString(output)));
        }
    }

    @Override
    public void handleTransportError(
            @NonNull final WebSocketSession session,
            @NonNull final Throwable exception
    ) throws Exception {
        log.warn("{} WebSocket transport error for session \"{}\": {}", LABEL, session.getId(), exception.getMessage());
        super.handleTransportError(session, exception);
    }

    private UserConnection createClientInbound(@NonNull final WebSocketSession session) {
        return new WebSocketSessionAdapter(session, jsonMapper);
    }
}

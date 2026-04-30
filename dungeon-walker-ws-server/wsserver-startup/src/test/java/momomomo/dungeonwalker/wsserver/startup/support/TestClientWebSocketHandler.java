package momomomo.dungeonwalker.wsserver.startup.support;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Component
@RequiredArgsConstructor
public class TestClientWebSocketHandler extends TextWebSocketHandler {

    @Getter
    private final State state = new State(
            new CopyOnWriteArrayList<>(),
            new CopyOnWriteArrayList<>(),
            new CopyOnWriteArrayList<>(),
            new CopyOnWriteArrayList<>(),
            new CopyOnWriteArrayList<>());

    @Override
    public void afterConnectionEstablished(@NonNull final WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        state.afterConnectionEstablished().add(session);
    }

    @Override
    public void afterConnectionClosed(
            @NonNull final WebSocketSession session,
            @NonNull final CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        state.afterConnectionClosed().add(Pair.of(session, status));
    }

    @Override
    public void handleTransportError(
            @NonNull final WebSocketSession session,
            @NonNull final Throwable exception) throws Exception {
        super.handleTransportError(session, exception);
        state.handleTransportError().add(Pair.of(session, exception));
    }

    @Override
    protected void handlePongMessage(
            @NonNull final WebSocketSession session,
            @NonNull final PongMessage message) throws Exception {
        super.handlePongMessage(session, message);
        state.handlePongMessage().add(Pair.of(session, message.getPayload().toString()));
    }

    @Override
    protected void handleTextMessage(
            @NonNull final WebSocketSession session,
            @NonNull final TextMessage message) throws Exception {
        super.handleTextMessage(session, message);
        state.handleTextMessage().add(Pair.of(session, message.getPayload()));
    }

    public record State(
            CopyOnWriteArrayList<WebSocketSession> afterConnectionEstablished,
            CopyOnWriteArrayList<Pair<WebSocketSession, CloseStatus>> afterConnectionClosed,
            CopyOnWriteArrayList<Pair<WebSocketSession, Throwable>> handleTransportError,
            CopyOnWriteArrayList<Pair<WebSocketSession, String>> handlePongMessage,
            CopyOnWriteArrayList<Pair<WebSocketSession, String>> handleTextMessage) {

        public void clear() {
            afterConnectionEstablished.clear();
            afterConnectionClosed.clear();
            handleTransportError.clear();
            handlePongMessage.clear();
            handleTextMessage.clear();
        }

    }

}

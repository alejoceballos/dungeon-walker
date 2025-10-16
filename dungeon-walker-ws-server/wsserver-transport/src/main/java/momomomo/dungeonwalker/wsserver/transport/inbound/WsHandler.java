package momomomo.dungeonwalker.wsserver.transport.inbound;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.wsserver.transport.outbound.WsSender;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static io.micrometer.common.util.StringUtils.isNotBlank;

@Slf4j
@Component
public class WsHandler extends TextWebSocketHandler {

    ConcurrentMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    ConcurrentMap<String, ScheduledFuture<?>> futures = new ConcurrentHashMap<>();

    ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    @PostConstruct
    public void init() {
        log.info("---> [WS Server - Handler] Bean created");
    }

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        log.info("---> [WS Server - Handler] Connection established with session {}", session.getUri());
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
        log.info("---> [WS Server - Handler] Connection closed for session {}", session.getId());

        sessions.remove(session.getId(), session);
        log.info("---> [WS Server - Handler] Session {} removed", session.getId());

        futures.get(session.getId()).cancel(true);
        log.info("---> [WS Server - Handler] Heartbeat task for session {} cancelled", session.getId());

        futures.remove(session.getId());
        log.info("---> [WS Server - Handler] Heartbeat task for session {} removed", session.getId());
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) {
        log.info("---> [WS Server - Handler] Session {} received the message {}",
                session.getId(),
                message.getPayload());

        if (!sessions.containsKey(session.getId()) && isNotBlank(message.getPayload())) {
            log.info("---> [WS Server - Handler] Session {} will be stored and won't accept any more messages", session.getId());
            sessions.put(session.getId(), session);

            ScheduledFuture<?> future = executor.scheduleAtFixedRate(() -> {
                        log.info("---> [WS Server - Handler] Will send heartbeat to session {}", sessions.get(session.getId()).getId());

                        new WsSender(sessions.get(session.getId()))
                                .send("10s heartbeat because you sent a message with %s"
                                        .formatted(message.getPayload()));
                    },
                    10,
                    10,
                    TimeUnit.SECONDS);
            futures.put(session.getId(), future);
        } else {
            log.info("---> [WS Server - Handler] Session {} won't accept more messages", sessions.get(session.getId()).getId());
        }
    }

}


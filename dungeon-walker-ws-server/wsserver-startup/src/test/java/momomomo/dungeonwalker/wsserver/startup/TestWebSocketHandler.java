package momomomo.dungeonwalker.wsserver.startup;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.commons.DateTimeManager;
import momomomo.dungeonwalker.wsserver.domain.input.ClientHeartbeat;
import momomomo.dungeonwalker.wsserver.domain.input.Input;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

@Slf4j
@Component
@RequiredArgsConstructor
public class TestWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper jsonMapper;
    private final DateTimeManager dateTimeManager;

    @Override
    public void afterConnectionEstablished(@NonNull final WebSocketSession session) {
        log.info("---> [WS Client - Test Handler] Connection established with session \"{}\"", session.getId());
        new Timer("WS Client - Test Timer")
                .scheduleAtFixedRate(
                        new TimerTask() {
                            @Override
                            public void run() {
                                log.info("---> [WS Client - Test Handler] Sending heartbeat for session \"{}\"", session.getId());

                                try {
                                    final var json = jsonMapper.writeValueAsString(
                                            Input.of(
                                                    new ClientHeartbeat("TBD", dateTimeManager.instantNow())));
                                    session.sendMessage(new TextMessage(json));

                                } catch (JsonProcessingException e) {
                                    log.error("---> [WS Client - Test Handler] Unable to transform to JSON: \"{}\"", e.getMessage());

                                } catch (IOException e) {
                                    log.error("---> [WS Client - Test Handler] Unable to send message: \"{}\"", e.getMessage());
                                }
                            }
                        },
                        10_000L,
                        10_000L);
    }

    @Override
    protected void handleTextMessage(
            @NonNull final WebSocketSession session,
            @NonNull final TextMessage message) {
        log.info("---> [WS Client - Test Handler] Received message \"{}\" for session \"{}\"",
                message.getPayload(),
                session.getId());
    }

}

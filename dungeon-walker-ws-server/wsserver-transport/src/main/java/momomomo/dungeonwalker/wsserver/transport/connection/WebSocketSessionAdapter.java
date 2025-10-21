package momomomo.dungeonwalker.wsserver.transport.connection;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import momomomo.dungeonwalker.wsserver.domain.connection.ClientConnection;
import momomomo.dungeonwalker.wsserver.domain.output.Output;
import momomomo.dungeonwalker.wsserver.transport.exception.WsServerTransportException;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.security.Principal;
import java.util.Optional;

@RequiredArgsConstructor
public class WebSocketSessionAdapter implements ClientConnection {

    private final WebSocketSession session;
    private final ObjectMapper jsonMapper;

    @Override
    public String getSessionId() {
        return session.getId();
    }

    @Override
    public String getUserId() {
        return Optional
                .ofNullable(session.getPrincipal())
                .map(Principal::getName)
                .orElse("guest-user");
    }

    @Override
    public void close() {
        try {
            session.close();
        } catch (final IOException e) {
            throw new WsServerTransportException(e);
        }
    }

    @Override
    public void send(@NonNull Output message) {
        try {
            final var output = jsonMapper.writeValueAsString(message);
            session.sendMessage(new TextMessage(output));

        } catch (final IOException e) {
            throw new WsServerTransportException(e);
        }
    }

}

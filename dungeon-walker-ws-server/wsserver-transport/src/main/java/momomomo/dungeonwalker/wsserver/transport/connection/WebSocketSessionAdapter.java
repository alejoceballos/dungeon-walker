package momomomo.dungeonwalker.wsserver.transport.connection;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.machinezoo.noexception.Exceptions;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import momomomo.dungeonwalker.commons.conditional.Conditional;
import momomomo.dungeonwalker.wsserver.domain.inbound.ClientConnection;
import momomomo.dungeonwalker.wsserver.domain.output.Output;
import momomomo.dungeonwalker.wsserver.transport.exception.WsServerTransportException;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.security.Principal;
import java.util.Optional;

@RequiredArgsConstructor
public class WebSocketSessionAdapter implements ClientConnection {

    private final WebSocketSession session;
    private final ObjectMapper jsonMapper;

    @NonNull
    @Override
    public String getSessionId() {
        return session.getId();
    }

    @NonNull
    @Override
    public String getUserId() {
        return Optional
                .ofNullable(session.getPrincipal())
                .map(Principal::getName)
                .orElse("guest-user");
    }

    @Override
    public void close() {
        Exceptions
                .wrap(WsServerTransportException::new)
                .run(session::close);
    }

    @Override
    public boolean isOpen() {
        return session.isOpen();
    }

    @Override
    public boolean isClosed() {
        return !session.isOpen();
    }

    @Override
    public void send(@NonNull final Output message) {
        Conditional
                .on(session::isOpen)
                .thenExecute(() -> Exceptions
                        .wrap(WsServerTransportException::new)
                        .run(() -> session.sendMessage(new TextMessage(jsonMapper.writeValueAsString(message)))))
                .evaluate();
    }

}

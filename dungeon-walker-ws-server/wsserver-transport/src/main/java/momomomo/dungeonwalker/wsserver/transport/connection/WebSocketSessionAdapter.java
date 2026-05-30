package momomomo.dungeonwalker.wsserver.transport.connection;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.machinezoo.noexception.Exceptions;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import momomomo.dungeonwalker.commons.conditional.Conditional;
import momomomo.dungeonwalker.wsserver.domain.data.user.output.Output;
import momomomo.dungeonwalker.wsserver.domain.outbound.UserConnection;
import momomomo.dungeonwalker.wsserver.transport.exception.WsServerTransportException;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Objects;

@RequiredArgsConstructor
public class WebSocketSessionAdapter implements UserConnection {

    private final WebSocketSession session;
    private final ObjectMapper jsonMapper;

    @NonNull
    @Override
    public String getId() {
        return session.getId();
    }

    @Override
    public boolean iConnected() {
        return session.isOpen();
    }

    @Override
    public void disconnect() {
        Conditional
                .on(this::iConnected)
                .thenExecute(() -> Exceptions
                        .wrap(WsServerTransportException::new)
                        .run(session::close))
                .evaluate();
    }

    @Override
    public void send(@NonNull final Output message) {
        Conditional
                .on(this::iConnected)
                .thenExecute(() -> Exceptions
                        .wrap(WsServerTransportException::new)
                        .run(() -> session.sendMessage(new TextMessage(jsonMapper.writeValueAsString(message)))))
                .evaluate();
    }

    @Override
    public boolean equals(final Object objectToCompare) {
        if (!(objectToCompare instanceof final WebSocketSessionAdapter that)) return false;
        return Objects.equals(session, that.session);
    }

    @Override
    public int hashCode() {
        return session.hashCode();
    }
}

package momomomo.dungeonwalker.wsserver.transport.connection;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.machinezoo.noexception.Exceptions;
import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import momomomo.dungeonwalker.commons.conditional.Conditional;
import momomomo.dungeonwalker.wsserver.domain.outbound.ClientOutbound;
import momomomo.dungeonwalker.wsserver.domain.data.client.output.Output;
import momomomo.dungeonwalker.wsserver.transport.exception.WsServerTransportException;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Builder
@RequiredArgsConstructor
public class WebSocketSessionAdapter implements ClientOutbound {

    private final WebSocketSession session;
    private final ObjectMapper jsonMapper;

    @NonNull
    @Override
    public String getId() {
        return session.getId();
    }

    @Override
    public void close() {
        Conditional
                .on(session::isOpen)
                .thenExecute(() -> Exceptions
                        .wrap(WsServerTransportException::new)
                        .run(session::close))
                .evaluate();
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

package momomomo.dungeonwalker.wsserver.transport.connection;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.machinezoo.noexception.Exceptions;
import jakarta.annotation.PreDestroy;
import lombok.NonNull;
import momomomo.dungeonwalker.commons.conditional.Conditional;
import momomomo.dungeonwalker.wsserver.domain.data.user.output.Output;
import momomomo.dungeonwalker.wsserver.domain.outbound.UserConnection;
import momomomo.dungeonwalker.wsserver.transport.exception.WsServerTransportException;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebSocketSessionAdapter implements UserConnection {

    private final WebSocketSession session;
    private final ObjectMapper jsonMapper;
    private final ExecutorService asyncExecutor;

    public WebSocketSessionAdapter(
            final WebSocketSession session,
            final ObjectMapper jsonMapper
    ) {
        this.session = session;
        this.jsonMapper = jsonMapper;
        this.asyncExecutor = Executors.newVirtualThreadPerTaskExecutor();
    }

    @PreDestroy
    private void onDestroy() {
        asyncExecutor.shutdown();
    }


    @NonNull
    @Override
    public String getId() {
        return session.getId();
    }

    @Override
    public boolean isConnected() {
        return session.isOpen();
    }

    @Override
    public void disconnect() {
        Conditional
                .on(this::isConnected)
                .thenExecute(() -> Exceptions
                        .wrap(WsServerTransportException::new)
                        .run(session::close))
                .evaluate();
    }

    @Override
    public void send(@NonNull final Output message) {
        Conditional
                .on(this::isConnected)
                .thenExecute(() -> sendAsync(message))
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

    private void sendAsync(final Output message) {
        if (asyncExecutor.isShutdown()) {
            return;
        }

        asyncExecutor.submit(() -> Exceptions
                .wrap(WsServerTransportException::new)
                .run(() -> session.sendMessage(new TextMessage(jsonMapper.writeValueAsString(message)))));
    }

}

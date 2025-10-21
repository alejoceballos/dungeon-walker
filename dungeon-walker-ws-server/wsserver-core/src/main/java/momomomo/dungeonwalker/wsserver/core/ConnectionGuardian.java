package momomomo.dungeonwalker.wsserver.core;

import akka.actor.typed.ActorSystem;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.wsserver.core.guardian.command.HandleMessage;
import momomomo.dungeonwalker.wsserver.core.guardian.command.RemoveConnection;
import momomomo.dungeonwalker.wsserver.core.guardian.command.EstablishConnection;
import momomomo.dungeonwalker.wsserver.core.guardian.command.GuardianCommand;
import momomomo.dungeonwalker.wsserver.domain.connection.ClientConnection;
import momomomo.dungeonwalker.wsserver.domain.connection.ConnectionManager;
import momomomo.dungeonwalker.wsserver.domain.input.Input;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ConnectionGuardian implements ConnectionManager {

    private final ActorSystem<GuardianCommand> guardian;

    public ConnectionGuardian(@NonNull final ActorSystem<GuardianCommand> guardian) {
        log.debug("[GUARDIAN - Manager] constructor");
        this.guardian = guardian;
    }

    @Override
    public void establish(@NonNull final ClientConnection connection) {
        log.debug("[GUARDIAN - Manager] Establish connection for user \"{}\" with session \"{}\"",
                connection.getUserId(),
                connection.getSessionId());

        guardian.tell(new EstablishConnection(connection));
    }

    @Override
    public void close(@NonNull final ClientConnection connection) {
        log.debug("[GUARDIAN - Manager] Close connection for user \"{}\" with session \"{}\"",
                connection.getUserId(),
                connection.getSessionId());

        guardian.tell(new RemoveConnection(connection));
    }

    @Override
    public void handleMessage(@NonNull final ClientConnection connection, @NonNull final Input message) {
        log.debug("[GUARDIAN - Manager] Message received for user \"{}\" with session \"{}\": {}",
                connection.getUserId(),
                connection.getSessionId(),
                message);

        guardian.tell(new HandleMessage(connection, message));
    }
}

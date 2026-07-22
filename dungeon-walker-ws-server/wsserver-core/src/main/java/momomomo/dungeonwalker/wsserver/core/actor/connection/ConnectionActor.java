package momomomo.dungeonwalker.wsserver.core.actor.connection;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.commons.conditional.Conditional;
import momomomo.dungeonwalker.wsserver.core.actor.client.ClientActor;
import momomomo.dungeonwalker.wsserver.core.actor.client.command.ClientCommand;
import momomomo.dungeonwalker.wsserver.core.actor.client.command.from.connection.ConnectionAuthenticatedCommand;
import momomomo.dungeonwalker.wsserver.core.actor.client.command.from.connection.ConnectionCloseCommand;
import momomomo.dungeonwalker.wsserver.core.actor.client.command.from.connection.ConnectionHeartbeatCommand;
import momomomo.dungeonwalker.wsserver.core.actor.client.command.from.connection.MoveCommand;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.ConnectionCommand;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.from.client.ClientDungeonCellStateChangedCommand;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.from.client.ClientDungeonStateChangedCommand;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.from.client.ClientErrorMessageCommand;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.from.client.ClientHeartbeatCommand;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.from.client.ClientMessageCommand;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.from.user.EstablishConnectionCommand;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.from.user.UserAuthenticateCommand;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.from.user.UserCloseCommand;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.from.user.UserHeartbeatCommand;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.from.user.UserMoveCommand;
import momomomo.dungeonwalker.wsserver.domain.auth.Authorizer;
import momomomo.dungeonwalker.wsserver.domain.auth.WsServerExpiredAuthorizationException;
import momomomo.dungeonwalker.wsserver.domain.data.user.output.AuthenticationResult;
import momomomo.dungeonwalker.wsserver.domain.data.user.output.ClientErrors;
import momomomo.dungeonwalker.wsserver.domain.data.user.output.Output;
import momomomo.dungeonwalker.wsserver.domain.data.user.output.ServerErrors;
import momomomo.dungeonwalker.wsserver.domain.data.user.output.ServerHeartbeat;
import momomomo.dungeonwalker.wsserver.domain.data.user.output.ServerMessage;
import momomomo.dungeonwalker.wsserver.domain.outbound.UserConnection;
import org.apache.pekko.actor.typed.Behavior;
import org.apache.pekko.actor.typed.PostStop;
import org.apache.pekko.actor.typed.javadsl.AbstractBehavior;
import org.apache.pekko.actor.typed.javadsl.ActorContext;
import org.apache.pekko.actor.typed.javadsl.Behaviors;
import org.apache.pekko.actor.typed.javadsl.Receive;
import org.apache.pekko.cluster.sharding.typed.javadsl.ClusterSharding;
import org.apache.pekko.cluster.sharding.typed.javadsl.EntityTypeKey;

import java.util.List;

import static java.util.Objects.nonNull;
import static momomomo.dungeonwalker.wsserver.core.actor.connection.ConnectionActor.STATE.AUTHENTICATED;
import static momomomo.dungeonwalker.wsserver.core.actor.connection.ConnectionActor.STATE.CONNECTED;
import static momomomo.dungeonwalker.wsserver.core.actor.connection.ConnectionActor.STATE.INITIALIZED;
import static momomomo.dungeonwalker.wsserver.core.actor.connection.ConnectionActor.STATE.UNINITIALIZED;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
public class ConnectionActor extends AbstractBehavior<ConnectionCommand> {

    @RequiredArgsConstructor
    enum STATE {
        UNINITIALIZED("Uninitialized"),
        INITIALIZED("Initialized"),
        CONNECTED("Connected"),
        AUTHENTICATED("Authenticated");

        @Getter
        private final String value;
    }

    private static final String LABEL = "---> [ACTOR - Connection]";

    public static final EntityTypeKey<ConnectionCommand> ENTITY_TYPE_KEY =
            EntityTypeKey.create(ConnectionCommand.class, ConnectionActor.class.getSimpleName() + "-type-key");

    private final Authorizer authorizer;
    private final ClusterSharding clusterSharding;

    private UserConnection userConnection;
    private String clientId;
    private STATE state;

    public ConnectionActor(
            @NonNull final ActorContext<ConnectionCommand> context,
            @NonNull final Authorizer authorizer,
            @NonNull final ClusterSharding clusterSharding
    ) {
        super(context);
        this.authorizer = authorizer;
        this.clusterSharding = clusterSharding;
        this.state = UNINITIALIZED;
    }

    public static Behavior<ConnectionCommand> create(
            @NonNull final Authorizer authorizer,
            @NonNull final ClusterSharding clusterSharding
    ) {
        log.debug("{} create", LABEL);

        return Behaviors.setup(context -> new ConnectionActor(
                context,
                authorizer,
                clusterSharding));
    }

    @Override
    public Receive<ConnectionCommand> createReceive() {
        log.debug(logShortMessage("Initial (Create/Receive) state"));

        state = INITIALIZED;

        return newReceiveBuilder()
                .onMessage(EstablishConnectionCommand.class, this::onEstablishConnection)
                .onAnyMessage(this::onInvalidCommand)
                .onSignal(PostStop.class, this::onPostStop)
                .build();
    }

    private Receive<ConnectionCommand> connected() {
        log.debug(logShortMessage("Connected state"));

        state = CONNECTED;

        return newReceiveBuilder()
                .onMessage(UserAuthenticateCommand.class, this::onUserAuthenticate)
                .onMessage(UserCloseCommand.class, this::onUserClose)
                .onAnyMessage(this::onInvalidCommand)
                .onSignal(PostStop.class, this::onPostStop)
                .build();
    }

    private Receive<ConnectionCommand> authenticated() {
        log.debug(logFullMessage("Authenticated state"));

        state = AUTHENTICATED;

        return newReceiveBuilder()
                .onMessage(UserCloseCommand.class, this::onUserClose)
                .onMessage(UserMoveCommand.class, this::onUserMove)
                .onMessage(UserHeartbeatCommand.class, this::onUserHeartbeat)
                .onMessage(ClientDungeonStateChangedCommand.class, this::onClientDungeonStateChanged)
                .onMessage(ClientDungeonCellStateChangedCommand.class, this::onClientDungeonCellStateChangedCommand)
                .onMessage(ClientMessageCommand.class, this::onClientMessage)
                .onMessage(ClientErrorMessageCommand.class, this::onClientErrorMessage)
                .onMessage(ClientHeartbeatCommand.class, this::onClientHeartbeat)
                .onSignal(PostStop.class, this::onPostStop)
                .build();
    }

    private Behavior<ConnectionCommand> onPostStop(@NonNull final PostStop signal) {
        log.debug(logFullMessage("on Post Stop: {}"), signal);

        if (nonNull(clientId)) {
            log.debug(logFullMessage("Telling the client to close the connection"));

            tellClient(new ConnectionCloseCommand("Closing connection"));
        }

        if (nonNull(userConnection) && userConnection.isConnected()) {
            log.debug(logFullMessage("Closing user connection: {}"), userConnection.getId());

            userConnection.send(Output.of(new ServerMessage("Disconnecting")));
            userConnection.disconnect();
        }

        return Behaviors.same();
    }

    private Behavior<ConnectionCommand> onEstablishConnection(@NonNull final EstablishConnectionCommand command) {
        log.debug(logShortMessage("on Establish Connection: {}"), command.userConnection().getId());

        userConnection = command.userConnection();
        userConnection.send(Output.of(new ServerMessage("Connected")));

        return connected();
    }

    private Behavior<ConnectionCommand> onInvalidCommand(@NonNull final ConnectionCommand command) {
        log.warn(logShortMessage("on Invalid Command: {}"), command);

        if (nonNull(userConnection) && userConnection.isConnected()) {
            userConnection.send(Output.of(
                    new ClientErrors(
                            List.of("Invalid \"%s\" message for state \"%s\"".formatted(
                                    command.getClass().getSimpleName(),
                                    state.getValue())))));
        }

        return Behaviors.stopped();
    }

    private Behavior<ConnectionCommand> onUserClose(@NonNull final UserCloseCommand command) {
        log.debug(logFullMessage("on User Close"));

        tellClient(new ConnectionCloseCommand("User closed the connection"));

        return Behaviors.stopped();
    }

    private Behavior<ConnectionCommand> onUserAuthenticate(@NonNull final UserAuthenticateCommand command) {
        log.debug(logFullMessage("on User Authenticate"));

        if (nonNull(clientId)) {
            log.warn(logFullMessage("Suspicious reauthentication attempt. Client already set"));

            tellClient(new ConnectionCloseCommand("Suspicious reauthentication attempt"));

            userConnection.send(Output.of(new ClientErrors(List.of("Already authenticated"))));

            return Behaviors.stopped();
        }

        try {
            clientId = authorizer.authorize(command.credentials());

        } catch (final WsServerExpiredAuthorizationException ex) {
            log.warn(logFullMessage("{}"), ex.getMessage());
        }

        if (isBlank(clientId)) {
            log.warn(logFullMessage("Unauthorized. Invalid token"));

            // No need to call the client actor, because without a client id there is no relation between inbound and client
            userConnection.send(Output.of(new ClientErrors(List.of("Unauthorized"))));
            return Behaviors.stopped();
        }

        tellClient(new ConnectionAuthenticatedCommand(userConnection.getId()));
        userConnection.send(Output.of(new AuthenticationResult(true)));

        return authenticated();
    }

    private Behavior<ConnectionCommand> onUserMove(@NonNull final UserMoveCommand command) {
        log.debug(logFullMessage("on User Move: {}"), command.direction());

        tellClient(new MoveCommand(command.direction()));

        return Behaviors.same();
    }

    private Behavior<ConnectionCommand> onUserHeartbeat(@NonNull final UserHeartbeatCommand command) {
        log.debug(logFullMessage("on User Heartbeat"));

        tellClient(new ConnectionHeartbeatCommand());

        return Behaviors.same();
    }

    private Behavior<ConnectionCommand> onClientDungeonStateChanged(
            @NonNull final ClientDungeonStateChangedCommand command
    ) {
        log.debug(logFullMessage("on Client Dungeon State Changed"));

        userConnection.send(Output.of(command.toDomain()));

        return Behaviors.same();
    }

    private Behavior<ConnectionCommand> onClientDungeonCellStateChangedCommand(
            @NonNull final ClientDungeonCellStateChangedCommand command
    ) {
        log.debug(logFullMessage("on Client Dungeon Cell State Changed"));

        userConnection.send(Output.of(command.toDomain()));

        return Behaviors.same();
    }

    private Behavior<ConnectionCommand> onClientHeartbeat(@NonNull final ClientHeartbeatCommand command) {
        log.debug(logShortMessage("on client heartbeat: {}"), command);

        userConnection.send(Output.of(new ServerHeartbeat()));

        return Behaviors.same();
    }

    private Behavior<ConnectionCommand> onClientMessage(@NonNull final ClientMessageCommand command) {
        log.debug(logShortMessage("on Pass Message To User: {}"), command);

        userConnection.send(Output.of(new ServerMessage(command.message())));

        return Behaviors.same();
    }

    private Behavior<ConnectionCommand> onClientErrorMessage(@NonNull final ClientErrorMessageCommand command) {
        log.debug(logShortMessage("on Pass Error To User: {}"), command);

        userConnection.send(Output.of(new ServerErrors(List.of(command.error()))));

        return Behaviors.same();
    }

    private void tellClient(@NonNull final ClientCommand command) {
        if (nonNull(clientId)) {
            clusterSharding.entityRefFor(ClientActor.ENTITY_TYPE_KEY, clientId).tell(command);

        } else {
            log.warn(
                    logShortMessage("Cannot tell {} to client. {} has no related {} yet"),
                    command.getClass().getSimpleName(),
                    this.getClass().getSimpleName(),
                    ClientActor.class.getSimpleName());
        }
    }

    private @NonNull String actorId() {
        return getContext().getSelf().path().name();
    }

    private @NonNull String logClientId() {
        return Conditional
                .on(() -> isNotBlank(clientId))
                .thenGet(() -> clientId)
                .orElseGet(() -> "** unavailable **")
                .evaluate();
    }

    private @NonNull String logShortMessage(@NonNull final String message) {
        return "%s[state: %s][actor: %s]: %s".formatted(LABEL, state.getValue(), actorId(), message);
    }

    private @NonNull String logFullMessage(@NonNull final String message) {
        return "%s[state: %s][actor: %s][client: %s]: %s".formatted(LABEL, state.getValue(), actorId(), logClientId(), message);
    }

}

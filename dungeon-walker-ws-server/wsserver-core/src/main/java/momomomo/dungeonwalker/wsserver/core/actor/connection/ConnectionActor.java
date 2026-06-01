package momomomo.dungeonwalker.wsserver.core.actor.connection;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.commons.conditional.Conditional;
import momomomo.dungeonwalker.wsserver.core.actor.client.ClientActor;
import momomomo.dungeonwalker.wsserver.core.actor.client.command.ClientCommand;
import momomomo.dungeonwalker.wsserver.core.actor.client.command.from.connection.ConnectionAuthenticatedCommand;
import momomomo.dungeonwalker.wsserver.core.actor.client.command.from.connection.ConnectionCloseCommand;
import momomomo.dungeonwalker.wsserver.core.actor.client.command.from.connection.ConnectionHeartbeatCommand;
import momomomo.dungeonwalker.wsserver.core.actor.client.command.from.connection.MoveCommand;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.ConnectionCommand;
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
import momomomo.dungeonwalker.wsserver.domain.data.user.output.ClientErrors;
import momomomo.dungeonwalker.wsserver.domain.data.user.output.Output;
import momomomo.dungeonwalker.wsserver.domain.data.user.output.ServerErrors;
import momomomo.dungeonwalker.wsserver.domain.data.user.output.ServerHeartbeat;
import momomomo.dungeonwalker.wsserver.domain.data.user.output.ServerMessage;
import momomomo.dungeonwalker.wsserver.domain.outbound.UserConnection;
import org.apache.pekko.actor.typed.Behavior;
import org.apache.pekko.actor.typed.javadsl.AbstractBehavior;
import org.apache.pekko.actor.typed.javadsl.ActorContext;
import org.apache.pekko.actor.typed.javadsl.Behaviors;
import org.apache.pekko.actor.typed.javadsl.Receive;
import org.apache.pekko.cluster.sharding.typed.javadsl.ClusterSharding;
import org.apache.pekko.cluster.sharding.typed.javadsl.EntityTypeKey;

import java.util.List;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
public class ConnectionActor extends AbstractBehavior<ConnectionCommand> {

    private static final String LABEL = "---> [ACTOR - Connection]";

    public static final EntityTypeKey<ConnectionCommand> ENTITY_TYPE_KEY =
            EntityTypeKey.create(ConnectionCommand.class, ConnectionActor.class.getSimpleName() + "-type-key");

    private final Authorizer authorizer;
    private final ClusterSharding clusterSharding;

    private UserConnection userConnection;
    private String clientId;

    public ConnectionActor(
            final ActorContext<ConnectionCommand> context,
            @NonNull final Authorizer authorizer,
            @NonNull final ClusterSharding clusterSharding
    ) {
        super(context);
        this.authorizer = authorizer;
        this.clusterSharding = clusterSharding;
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

        return newReceiveBuilder()
                .onMessage(EstablishConnectionCommand.class, this::onEstablishConnection)
                .onAnyMessage(this::onInvalidCommandWhenInitialized)
                .build();
    }

    private Receive<ConnectionCommand> connected() {
        log.debug(logShortMessage("Connected state"));

        return newReceiveBuilder()
                .onMessage(UserAuthenticateCommand.class, this::onUserAuthenticate)
                .onMessage(UserCloseCommand.class, this::onUserClose)
                .onAnyMessage(this::onInvalidCommandWhenConnected)
                .build();
    }

    private Receive<ConnectionCommand> authenticated() {
        log.debug(logFullMessage("Authenticated state"));

        return newReceiveBuilder()
                .onMessage(UserCloseCommand.class, this::onUserClose)
                .onMessage(UserMoveCommand.class, this::onUserMove)
                .onMessage(UserHeartbeatCommand.class, this::onUserHeartbeat)
                .onMessage(ClientDungeonStateChangedCommand.class, this::onClientDungeonStateChanged)
                .onMessage(ClientMessageCommand.class, this::onClientMessage)
                .onMessage(ClientErrorMessageCommand.class, this::onClientErrorMessage)
                .onMessage(ClientHeartbeatCommand.class, this::onClientHeartbeat)
                .onAnyMessage(this::onInvalidCommandWhenAuthenticated)
                .build();
    }

    private Behavior<ConnectionCommand> onEstablishConnection(final EstablishConnectionCommand command) {
        log.trace(logShortMessage("on Establish Client Connection: {}"), command.userConnection().getId());

        userConnection = command.userConnection();
        userConnection.send(Output.of(new ServerMessage("Connected")));

        return connected();
    }

    private Behavior<ConnectionCommand> onInvalidCommandWhenInitialized(final ConnectionCommand command) {
        log.warn(logShortMessage("on Invalid Command When Initialized: {}"), command);
        // No need to send anything to websocket client, since there is no outbound
        // No need to send anything to client actor, since there is no client id
        return Behaviors.stopped();
    }

    private Behavior<ConnectionCommand> onUserClose(final UserCloseCommand command) {
        log.trace(logFullMessage("on Close Client Connection"));

        if (nonNull(clientId)) {
            tellClient(new ConnectionCloseCommand("Connection closed by client"));
        }

        if (nonNull(userConnection) && userConnection.iConnected()) {
            userConnection.send(Output.of(new ServerMessage("Disconnecting")));
            userConnection.disconnect();
        }

        return Behaviors.stopped();
    }

    private Behavior<ConnectionCommand> onUserAuthenticate(final UserAuthenticateCommand command) {
        log.trace(logFullMessage("on Authenticate Client"));

        if (nonNull(clientId)) {
            tellClient(new ConnectionCloseCommand("Suspicious reauthentication attempt"));

            userConnection.send(Output.of(new ClientErrors(List.of("Already authenticated. Disconnecting"))));
            userConnection.disconnect();

            return Behaviors.stopped();
        }

        clientId = authorizer.authorize(command.credentials());

        if (isBlank(clientId)) {
            // No need to call the client actor, because without a client id there is no relation between inbound and client
            userConnection.send(Output.of(new ClientErrors(List.of("Unauthorized. Disconnecting"))));
            userConnection.disconnect();

            return Behaviors.stopped();
        }

        tellClient(new ConnectionAuthenticatedCommand(userConnection.getId()));

        return authenticated();
    }

    private Behavior<ConnectionCommand> onInvalidCommandWhenConnected(final ConnectionCommand command) {
        log.trace(logFullMessage("on Invalid Command When Connected: {}"), command);

        userConnection.send(Output.of(
                new ClientErrors(
                        List.of("Invalid %s message after connected. Disconnecting".formatted(
                                command.getClass().getSimpleName())))));
        userConnection.disconnect();

        return Behaviors.stopped();
    }

    private Behavior<ConnectionCommand> onUserMove(final UserMoveCommand command) {
        log.trace(logFullMessage("on Move Client: {}"), command.direction());

        tellClient(new MoveCommand(command.direction()));

        return Behaviors.same();
    }

    private Behavior<ConnectionCommand> onUserHeartbeat(final UserHeartbeatCommand command) {
        log.trace(logFullMessage("on Pass By Client Heartbeat"));

        tellClient(new ConnectionHeartbeatCommand());

        return Behaviors.same();
    }

    private Behavior<ConnectionCommand> onClientDungeonStateChanged(final ClientDungeonStateChangedCommand command) {
        log.trace(logFullMessage("on Pass Dungeon State To User"));

        userConnection.send(Output.of(command.toDomain()));

        return Behaviors.same();
    }

    private Behavior<ConnectionCommand> onInvalidCommandWhenAuthenticated(final ConnectionCommand command) {
        log.trace(logShortMessage("on Invalid Command When Authenticated: {}"), command);

        final var commandClass = command.getClass().getSimpleName();

        tellClient(new ConnectionCloseCommand("Suspicious %s command attempt".formatted(commandClass)));

        userConnection.send(Output.of(
                new ClientErrors(
                        List.of("Invalid %s message after authenticated. Disconnecting".formatted(commandClass)))));
        userConnection.disconnect();

        return Behaviors.stopped();
    }

    private Behavior<ConnectionCommand> onClientHeartbeat(final ClientHeartbeatCommand command) {
        log.trace(logShortMessage("on client heartbeat: {}"), command);

        userConnection.send(Output.of(new ServerHeartbeat()));

        return Behaviors.same();
    }

    private Behavior<ConnectionCommand> onClientMessage(final ClientMessageCommand command) {
        log.trace(logShortMessage("on Pass Message To User: {}"), command);

        userConnection.send(Output.of(new ServerMessage(command.message())));

        return Behaviors.same();
    }

    private Behavior<ConnectionCommand> onClientErrorMessage(final ClientErrorMessageCommand command) {
        log.trace(logShortMessage("on Pass Error To User: {}"), command);

        userConnection.send(Output.of(new ServerErrors(List.of(command.error()))));

        return Behaviors.same();
    }

    private void tellClient(final ClientCommand command) {
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

    private @NonNull String logShortMessage(final String message) {
        return "%s[actor: %s]: %s".formatted(LABEL, actorId(), message);
    }

    private @NonNull String logFullMessage(final String message) {
        return "%s[actor: %s][client: %s]: %s".formatted(LABEL, actorId(), logClientId(), message);
    }

}

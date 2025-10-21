package momomomo.dungeonwalker.wsserver.core.guardian;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.commons.Conditional;
import momomomo.dungeonwalker.commons.DateTimeManager;
import momomomo.dungeonwalker.wsserver.core.config.HeartbeatConfig;
import momomomo.dungeonwalker.wsserver.core.connection.ConnectionActor;
import momomomo.dungeonwalker.wsserver.core.connection.command.CloseConnection;
import momomomo.dungeonwalker.wsserver.core.connection.command.ConnectionCommand;
import momomomo.dungeonwalker.wsserver.core.connection.command.ResetConnection;
import momomomo.dungeonwalker.wsserver.core.connection.command.SendMessageFromClient;
import momomomo.dungeonwalker.wsserver.core.connection.command.SetConnection;
import momomomo.dungeonwalker.wsserver.core.guardian.command.EstablishConnection;
import momomomo.dungeonwalker.wsserver.core.guardian.command.GuardianCommand;
import momomomo.dungeonwalker.wsserver.core.guardian.command.HandleMessage;
import momomomo.dungeonwalker.wsserver.core.guardian.command.RemoveConnection;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Slf4j
public class GuardianActor extends AbstractBehavior<GuardianCommand> {

    private final Map<String, ActorRef<ConnectionCommand>> connectionActors = new HashMap<>();
    private final DateTimeManager dateTimeManager;
    private final HeartbeatConfig heartbeatConfig;

    private GuardianActor(
            final ActorContext<GuardianCommand> context,
            final DateTimeManager dateTimeManager,
            final HeartbeatConfig heartbeatConfig) {
        super(context);
        this.dateTimeManager = dateTimeManager;
        this.heartbeatConfig = heartbeatConfig;
    }

    public static Behavior<GuardianCommand> create(
            final DateTimeManager dateTimeManager,
            final HeartbeatConfig heartbeatConfig) {
        log.debug("[ACTOR - Guardian] create");
        return Behaviors.setup(context -> new GuardianActor(context, dateTimeManager, heartbeatConfig));
    }

    @Override
    public Receive<GuardianCommand> createReceive() {
        log.debug("[ACTOR - Guardian] create receive");
        return newReceiveBuilder()
                .onMessage(EstablishConnection.class, this::onEstablishConnection)
                .onMessage(RemoveConnection.class, this::onRemoveConnection)
                .onMessage(HandleMessage.class, this::onHandleMessage)
                .build();
    }

    private Behavior<GuardianCommand> onEstablishConnection(final EstablishConnection command) {
        final var userId = command.connection().getUserId();
        log.debug("[ACTOR - Guardian] on create connection \"{}\" for user \"{}\" ", command.connection().getSessionId(), userId);

        Conditional.when(connectionActors.get(userId))
                .isNull(_ -> {
                    connectionActors.put(
                            userId,
                            getContext().spawn(
                                    ConnectionActor.create(dateTimeManager, heartbeatConfig),
                                    command.connection().getUserId()));
                    connectionActors.get(userId).tell(new SetConnection(command.connection()));
                })
                .isNotNull(actorRef -> actorRef.tell(new ResetConnection(command.connection())));

        return Behaviors.same();
    }

    private Behavior<GuardianCommand> onRemoveConnection(final RemoveConnection command) {
        final var userId = command.connection().getUserId();
        log.debug("[ACTOR - Guardian] on remove connection \"{}\" for user \"{}\"", command.connection().getSessionId(), userId);

        Conditional.when(connectionActors.get(userId))
                .isNull(logNoConnectionFoundForUser(userId))
                .isNotNull(actorRef -> {
                    actorRef.tell(new CloseConnection(command.connection()));
                    connectionActors.remove(userId);
                });

        return Behaviors.same();
    }

    private Behavior<GuardianCommand> onHandleMessage(final HandleMessage command) {
        final var userId = command.connection().getUserId();
        log.debug("[ACTOR - Guardian] on handle message for user \"{}\": \"{}\"", userId, command.message());

        Conditional.when(connectionActors.get(userId))
                .isNull(logNoConnectionFoundForUser(userId))
                .isNotNull(actorRef -> actorRef.tell(new SendMessageFromClient(command.message())));

        return Behaviors.same();
    }

    private static Consumer<Void> logNoConnectionFoundForUser(final String userId) {
        return _ -> log.error("[ACTOR - Guardian] no actor found for user \"{}\"", userId);
    }


}

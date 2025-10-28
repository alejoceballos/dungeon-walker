package momomomo.dungeonwalker.wsserver.core.sctor.guardian;

import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GuardianActor extends AbstractBehavior<Void> {

    private GuardianActor(@NonNull final ActorContext<Void> context) {
        super(context);
    }

    public static Behavior<Void> create() {
        log.debug("[ACTOR - Guardian] create");
        return Behaviors.setup(GuardianActor::new);
    }

    @Override
    public Receive<Void> createReceive() {
        log.debug("[ACTOR - Guardian] create receive");
        return newReceiveBuilder()
                .onSignal(PostStop.class, this::onPostStop)
                .build();
    }

    private Behavior<Void> onPostStop(final PostStop signal) {
        log.debug("[ACTOR - Guardian] on post stop");
        return Behaviors.stopped();
    }

}

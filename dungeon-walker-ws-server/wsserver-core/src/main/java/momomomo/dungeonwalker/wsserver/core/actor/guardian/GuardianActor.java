package momomomo.dungeonwalker.wsserver.core.actor.guardian;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.pekko.actor.typed.Behavior;
import org.apache.pekko.actor.typed.PostStop;
import org.apache.pekko.actor.typed.javadsl.AbstractBehavior;
import org.apache.pekko.actor.typed.javadsl.ActorContext;
import org.apache.pekko.actor.typed.javadsl.Behaviors;
import org.apache.pekko.actor.typed.javadsl.Receive;

@Slf4j
public class GuardianActor extends AbstractBehavior<Void> {

    private GuardianActor(@NonNull final ActorContext<Void> context) {
        super(context);
    }

    public static Behavior<Void> create() {
        log.debug("---> [ACTOR - Guardian] create");
        return Behaviors.setup(GuardianActor::new);
    }

    @Override
    public Receive<Void> createReceive() {
        log.debug("---> [ACTOR - Guardian] create receive");
        return newReceiveBuilder()
                .onSignal(PostStop.class, this::onPostStop)
                .build();
    }

    private Behavior<Void> onPostStop(final PostStop signal) {
        log.debug("---> [ACTOR - Guardian] on post stop");
        return Behaviors.stopped();
    }

}

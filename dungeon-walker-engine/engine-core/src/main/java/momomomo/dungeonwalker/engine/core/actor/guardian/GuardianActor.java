package momomomo.dungeonwalker.engine.core.actor.guardian;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.engine.core.actor.TopicWrapper;
import momomomo.dungeonwalker.engine.core.actor.walker.command.WalkerCommand;
import org.apache.pekko.actor.typed.Behavior;
import org.apache.pekko.actor.typed.PostStop;
import org.apache.pekko.actor.typed.javadsl.AbstractBehavior;
import org.apache.pekko.actor.typed.javadsl.ActorContext;
import org.apache.pekko.actor.typed.javadsl.Behaviors;
import org.apache.pekko.actor.typed.javadsl.Receive;
import org.apache.pekko.actor.typed.pubsub.Topic;

@Slf4j
public class GuardianActor extends AbstractBehavior<Void> {

    private GuardianActor(
            @NonNull final ActorContext<Void> context,
            @NonNull final TopicWrapper topicWrapper
    ) {
        super(context);

        topicWrapper.setWalkerBroadcastTopic(
                context.spawn(
                        Topic.create(WalkerCommand.class, "walker-broadcast"),
                        "walker-broadcast-topic"));
    }

    public static Behavior<Void> create(@NonNull final TopicWrapper topicWrapper) {
        log.debug("---> [ACTOR - Engine] create");
        return Behaviors.setup(context -> new GuardianActor(context, topicWrapper));
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

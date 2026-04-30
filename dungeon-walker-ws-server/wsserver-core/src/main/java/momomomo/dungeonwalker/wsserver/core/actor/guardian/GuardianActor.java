package momomomo.dungeonwalker.wsserver.core.actor.guardian;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.wsserver.core.actor.TopicWrapper;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.ConnectionCommand;
import org.apache.pekko.actor.typed.Behavior;
import org.apache.pekko.actor.typed.PostStop;
import org.apache.pekko.actor.typed.javadsl.AbstractBehavior;
import org.apache.pekko.actor.typed.javadsl.ActorContext;
import org.apache.pekko.actor.typed.javadsl.Behaviors;
import org.apache.pekko.actor.typed.javadsl.Receive;
import org.apache.pekko.actor.typed.pubsub.Topic;

@Slf4j
public class GuardianActor extends AbstractBehavior<Void> {

    private static final String LABEL = "---> [ACTOR - Guardian]";

    private GuardianActor(
            @NonNull final ActorContext<Void> context,
            @NonNull final TopicWrapper topicWrapper) {
        super(context);

        topicWrapper.setConnectionBroadcastTopic(
                context.spawn(
                        Topic.create(ConnectionCommand.class, "connection-broadcast"),
                        "connection-broadcast-topic"));
    }

    public static Behavior<Void> create(@NonNull final TopicWrapper topicWrapper) {
        log.debug("{} create", LABEL);
        return Behaviors.setup(context -> new GuardianActor(context, topicWrapper));
    }

    @Override
    public Receive<Void> createReceive() {
        log.debug("{} create receive", LABEL);
        return newReceiveBuilder()
                .onSignal(PostStop.class, this::onPostStop)
                .build();
    }

    private Behavior<Void> onPostStop(final PostStop signal) {
        log.debug("{} on post stop", LABEL);
        return Behaviors.stopped();
    }

}

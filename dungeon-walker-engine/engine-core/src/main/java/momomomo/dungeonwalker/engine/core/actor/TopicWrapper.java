package momomomo.dungeonwalker.engine.core.actor;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import momomomo.dungeonwalker.engine.core.actor.walker.command.WalkerCommand;
import org.apache.pekko.actor.typed.ActorRef;
import org.apache.pekko.actor.typed.pubsub.Topic;

@NoArgsConstructor
public class TopicWrapper {

    @Getter
    @Setter
    private ActorRef<Topic.Command<WalkerCommand>> walkerBroadcastTopic;

}

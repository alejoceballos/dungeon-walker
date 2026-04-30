package momomomo.dungeonwalker.wsserver.core.actor;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.ConnectionCommand;
import org.apache.pekko.actor.typed.ActorRef;
import org.apache.pekko.actor.typed.pubsub.Topic;

@NoArgsConstructor
public class TopicWrapper {

    @Getter
    @Setter
    private ActorRef<Topic.Command<ConnectionCommand>> connectionBroadcastTopic;

}

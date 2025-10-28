package momomomo.dungeonwalker.wsserver.transport.outbound;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.clientrequest.AddClientWalkerProto;
import momomomo.dungeonwalker.wsserver.domain.outbound.Sender;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PulsarPublisher implements Sender<AddClientWalkerProto.AddClientWalker> {

    public void send(@NonNull AddClientWalkerProto.AddClientWalker message) {
        // TODO: Publish to Pulsar
    }

}

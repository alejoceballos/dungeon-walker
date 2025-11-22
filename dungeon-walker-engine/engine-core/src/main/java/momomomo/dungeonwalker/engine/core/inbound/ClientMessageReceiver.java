package momomomo.dungeonwalker.engine.core.inbound;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.clientrequest.AddClientWalkerProto;
import momomomo.dungeonwalker.engine.domain.DungeonMaster;
import momomomo.dungeonwalker.engine.domain.inbound.MessageReceiver;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClientMessageReceiver implements MessageReceiver<AddClientWalkerProto.AddClientWalker> {

    public final DungeonMaster dungeonMaster;

    @Override
    public void onReceive(final AddClientWalkerProto.AddClientWalker message) {
        log.debug("---> [MESSAGE RECEIVER - Client] Message received: \"{}\"", message);
        dungeonMaster.enterTheDungeon(message.getId());
    }

}

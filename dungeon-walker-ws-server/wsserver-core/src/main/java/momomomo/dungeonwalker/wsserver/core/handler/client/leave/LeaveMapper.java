package momomomo.dungeonwalker.wsserver.core.handler.client.leave;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.contract.client.ClientRequestProto.ClientRequest;
import momomomo.dungeonwalker.contract.client.LeaveDungeonProto;
import momomomo.dungeonwalker.wsserver.core.handler.client.InputDataMapper;
import momomomo.dungeonwalker.wsserver.domain.input.client.Leave;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LeaveMapper implements InputDataMapper<Leave, ClientRequest> {

    private static final String LABEL = "---> [MAPPER - Leave]";

    @Override
    @NonNull
    public ClientRequest map(@NonNull final Leave inputData) {
        log.debug("{} mapping \"{}\"", LABEL, inputData);

        return ClientRequest
                .newBuilder()
                .setClientId(inputData.clientId())
                .setLeaveDungeon(LeaveDungeonProto.LeaveDungeon.newBuilder().build())
                .build();
    }

}

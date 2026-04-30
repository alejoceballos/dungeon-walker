package momomomo.dungeonwalker.wsserver.core.handler.client.identity;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.contract.client.ClientRequestProto.ClientRequest;
import momomomo.dungeonwalker.contract.client.ConnectionProto.Connection;
import momomomo.dungeonwalker.wsserver.core.handler.client.InputDataMapper;
import momomomo.dungeonwalker.wsserver.domain.input.client.Identity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class IdentityMapper implements InputDataMapper<Identity, ClientRequest> {

    private static final String LABEL = "---> [MAPPER - Identity]";

    @Override
    @NonNull
    public ClientRequest map(@NonNull final Identity inputData) {
        log.debug("{} mapping \"{}\"", LABEL, inputData);

        return ClientRequest
                .newBuilder()
                .setClientId(inputData.clientId())
                .setConnection(Connection.newBuilder().build())
                .build();
    }

}

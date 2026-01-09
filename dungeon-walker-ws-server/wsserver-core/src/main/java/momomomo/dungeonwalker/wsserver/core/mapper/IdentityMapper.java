package momomomo.dungeonwalker.wsserver.core.mapper;

import jakarta.annotation.Nonnull;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.contract.client.ClientRequestProto.ClientRequest;
import momomomo.dungeonwalker.contract.client.ConnectionProto.Connection;
import momomomo.dungeonwalker.wsserver.domain.input.Identity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class IdentityMapper implements InputDataMapper<Identity, ClientRequest> {

    @Override
    @Nonnull
    public ClientRequest map(@NonNull final Identity inputData) {
        log.debug("---> [MAPPER - Identity] mapping \"{}\"", inputData);

        return ClientRequest.newBuilder()
                .setClientId(inputData.clientId())
                .setConnection(Connection.newBuilder().build())
                .build();
    }

}

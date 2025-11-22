package momomomo.dungeonwalker.wsserver.core.mapper;

import jakarta.annotation.Nonnull;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.clientrequest.AddClientWalkerProto;
import momomomo.dungeonwalker.wsserver.domain.input.Identity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class IdentityMapper implements InputDataMapper<Identity, AddClientWalkerProto.AddClientWalker> {

    @Nonnull
    @Override
    public AddClientWalkerProto.AddClientWalker map(@NonNull final Identity inputData) {
        log.debug("---> [MAPPER - Identity] mapping \"{}\"", inputData);
        return AddClientWalkerProto.AddClientWalker.newBuilder()
                .setId(inputData.id())
                .build();
    }

}

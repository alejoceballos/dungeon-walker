package momomomo.dungeonwalker.engine.core.inbound.client.mapper;

import lombok.NonNull;
import momomomo.dungeonwalker.contract.client.ClientRequestProto.ClientRequest;
import momomomo.dungeonwalker.engine.core.actor.walker.command.WalkerCommand;

public interface ClientRequestMapper<O extends WalkerCommand> {

    @NonNull
    O map(@NonNull ClientRequest request);

    boolean canMap(@NonNull ClientRequest request);

}

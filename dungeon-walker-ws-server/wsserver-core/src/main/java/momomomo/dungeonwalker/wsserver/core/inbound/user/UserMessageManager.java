package momomomo.dungeonwalker.wsserver.core.inbound.user;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.wsserver.core.actor.ClusterShardingManager;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.ConnectionCommand;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.from.user.EstablishConnectionCommand;
import momomomo.dungeonwalker.wsserver.core.actor.connection.command.from.user.UserCloseCommand;
import momomomo.dungeonwalker.wsserver.core.inbound.user.mapper.UserInputMapperSelector;
import momomomo.dungeonwalker.wsserver.domain.data.user.input.Input;
import momomomo.dungeonwalker.wsserver.domain.inbound.UserInbound;
import momomomo.dungeonwalker.wsserver.domain.outbound.UserConnection;
import org.springframework.stereotype.Component;

import static java.util.Objects.nonNull;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserMessageManager implements UserInbound {

    private static final String LABEL = "---> [CLIENT INBOUND - Manager]";

    private final ClusterShardingManager clusterShardingManager;
    private final UserInputMapperSelector mapperSelector;

    @Override
    public void establish(@NonNull final UserConnection userConnection) {
        log.debug("{} Establish connection \"{}\"", LABEL, userConnection.getId());
        tellConnectionTo(userConnection, new EstablishConnectionCommand(userConnection));
    }

    @Override
    public void close(@NonNull final UserConnection userConnection) {
        log.debug("{} Close connection \"{}\"", LABEL, userConnection.getId());
        tellConnectionTo(userConnection, new UserCloseCommand());
    }

    @Override
    public void handle(@NonNull final UserConnection userConnection, @NonNull final Input input) {
        log.debug("{} Message received for connection \"{}\": {}", LABEL, userConnection.getId(), input);

        final var mapper = mapperSelector.select(input.data());

        if (nonNull(mapper)) {
            tellConnectionTo(userConnection, mapper.map(input.data()));

        } else {
            log.error("{} No mapper found for input: {}", LABEL, input);
        }
    }

    private void tellConnectionTo(final UserConnection userConnection, final ConnectionCommand command) {
        clusterShardingManager.getConnectionRef(userConnection.getId()).tell(command);
    }

}

package momomomo.dungeonwalker.wsserver.core.validator;

import jakarta.annotation.Nonnull;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.wsserver.domain.input.ClientHeartbeat;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class HeartbeatValidator implements InputDataValidator<ClientHeartbeat> {

    @Nonnull
    @Override
    public List<ValidationError> validate(@NonNull final ClientHeartbeat inputData) {
        throw new UnsupportedOperationException("Heartbeat messages are not supposed to be validated.");
    }
}

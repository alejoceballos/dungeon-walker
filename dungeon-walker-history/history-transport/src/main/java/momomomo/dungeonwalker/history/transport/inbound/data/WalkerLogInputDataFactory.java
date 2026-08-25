package momomomo.dungeonwalker.history.transport.inbound.data;

import jakarta.annotation.Nullable;
import lombok.NonNull;
import momomomo.dungeonwalker.history.domain.inbound.data.CoordinatesLogInput;
import momomomo.dungeonwalker.history.domain.inbound.data.WalkerLogInput;
import momomomo.dungeonwalker.history.domain.inbound.data.WalkerLogInputFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class WalkerLogInputDataFactory implements WalkerLogInputFactory {

    @Override
    public @NonNull WalkerLogInput createEnterLog(
            @NonNull final Instant timestamp,
            @NonNull final String walkerId,
            @Nullable final CoordinatesLogInput coordinates
    ) {
        return WalkerLogInputData
                .builder()
                .timestamp(timestamp)
                .walkerId(walkerId)
                .to(coordinates)
                .build();
    }

    @Override
    public @NonNull WalkerLogInput createMoveLog(
            @NonNull final Instant timestamp,
            @NonNull final String walkerId,
            @Nullable final CoordinatesLogInput from,
            @Nullable final CoordinatesLogInput to
    ) {
        return WalkerLogInputData
                .builder()
                .timestamp(timestamp)
                .walkerId(walkerId)
                .from(from)
                .to(to)
                .build();
    }

    @Override
    public @NonNull WalkerLogInput createLeaveLog(
            @NonNull final Instant timestamp,
            @NonNull final String walkerId,
            @Nullable final String reason
    ) {
        return WalkerLogInputData
                .builder()
                .timestamp(timestamp)
                .walkerId(walkerId)
                .reason(reason)
                .build();
    }
    
}

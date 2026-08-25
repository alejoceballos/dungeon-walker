package momomomo.dungeonwalker.history.domain.inbound.data;

import jakarta.annotation.Nullable;
import lombok.NonNull;

import java.time.Instant;

public interface WalkerLogInputFactory {

    @NonNull
    WalkerLogInput createEnterLog(
            @NonNull Instant timestamp,
            @NonNull String walkerId,
            @Nullable CoordinatesLogInput coordinates);

    @NonNull
    WalkerLogInput createMoveLog(
            @NonNull Instant timestamp,
            @NonNull String walkerId,
            @Nullable CoordinatesLogInput from,
            @Nullable CoordinatesLogInput to);

    @NonNull
    WalkerLogInput createLeaveLog(
            @NonNull Instant timestamp,
            @NonNull String walkerId,
            @Nullable String reason);

}

package momomomo.dungeonwalker.engine.domain.outbound;

import jakarta.annotation.Nullable;
import lombok.NonNull;
import momomomo.dungeonwalker.engine.domain.model.coordinates.Coordinates;

import java.time.Instant;

public record WalkerHistoryLog(
        @NonNull Type type,
        @NonNull String walkerId,
        @NonNull Instant timestamp,
        @Nullable Coordinates from,
        @Nullable Coordinates to,
        @Nullable String notes
) {

    public enum Type {
        ENTER,
        LEAVE,
        MOVE
    }

    public static WalkerHistoryLog leave(
            @NonNull final String walkerId,
            @NonNull final Instant timestamp,
            @NonNull final String notes
    ) {
        return new WalkerHistoryLog(Type.LEAVE, walkerId, timestamp, null, null, notes);
    }

    public static WalkerHistoryLog enter(
            @NonNull final String walkerId,
            @NonNull final Instant timestamp,
            @NonNull final Coordinates coordinates
    ) {
        return new WalkerHistoryLog(Type.ENTER, walkerId, timestamp, null, coordinates, null);
    }

    public static WalkerHistoryLog move(
            @NonNull final String walkerId,
            @NonNull final Instant timestamp,
            @NonNull final Coordinates from,
            @NonNull final Coordinates to
    ) {
        return new WalkerHistoryLog(Type.LEAVE, walkerId, timestamp, from, to, null);
    }

}

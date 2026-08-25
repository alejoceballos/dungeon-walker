package momomomo.dungeonwalker.contract.history.walker;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import momomomo.dungeonwalker.contract.history.LogCoordinates;

import java.time.Instant;

@ToString(callSuper = true, includeFieldNames = false)
@Getter
@Setter
@NoArgsConstructor
public class MoveLog extends WalkerLog {

    public MoveLog(
            @NonNull final Instant timestamp,
            @NonNull final String walkerId,
            @NonNull final LogCoordinates from,
            @NonNull final LogCoordinates to
    ) {
        super(timestamp, walkerId);
        this.from = from;
        this.to = to;
    }

    private LogCoordinates from;
    private LogCoordinates to;

}

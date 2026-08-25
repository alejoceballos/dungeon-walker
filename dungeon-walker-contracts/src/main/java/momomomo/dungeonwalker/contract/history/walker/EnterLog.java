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
public class EnterLog extends WalkerLog {

    private LogCoordinates startCoordinates;

    public EnterLog(
            @NonNull final Instant timestamp,
            @NonNull final String walkerId,
            @NonNull final LogCoordinates startCoordinates
    ) {
        super(timestamp, walkerId);
        this.startCoordinates = startCoordinates;
    }

}

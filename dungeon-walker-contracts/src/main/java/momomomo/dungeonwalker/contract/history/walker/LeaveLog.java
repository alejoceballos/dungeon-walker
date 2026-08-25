package momomomo.dungeonwalker.contract.history.walker;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

@ToString(callSuper = true, includeFieldNames = false)
@Getter
@Setter
@NoArgsConstructor
public class LeaveLog extends WalkerLog {

    public LeaveLog(
            @NonNull final Instant timestamp,
            @NonNull final String walkerId,
            @NonNull final String reason) {
        super(timestamp, walkerId);
        this.reason = reason;
    }

    private String reason;

}

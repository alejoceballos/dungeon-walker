package momomomo.dungeonwalker.contract.history.walker;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import momomomo.dungeonwalker.contract.history.HistoryLog;

import java.time.Instant;

@ToString(callSuper = true, includeFieldNames = false)
@Getter
@Setter
@NoArgsConstructor
public abstract class WalkerLog extends HistoryLog {

    private String walkerId;

    protected WalkerLog(final Instant timestamp, final String walkerId) {
        super(timestamp);
        this.walkerId = walkerId;
    }

}

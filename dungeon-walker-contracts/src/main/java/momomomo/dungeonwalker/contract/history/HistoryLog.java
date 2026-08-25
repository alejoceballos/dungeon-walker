package momomomo.dungeonwalker.contract.history;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import momomomo.dungeonwalker.contract.history.walker.EnterLog;
import momomomo.dungeonwalker.contract.history.walker.LeaveLog;
import momomomo.dungeonwalker.contract.history.walker.MoveLog;

import java.time.Instant;

@ToString(includeFieldNames = false)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes({
        @JsonSubTypes.Type(value = EnterLog.class),
        @JsonSubTypes.Type(value = LeaveLog.class),
        @JsonSubTypes.Type(value = MoveLog.class)
})
public abstract class HistoryLog {

    private Instant timestamp;

}

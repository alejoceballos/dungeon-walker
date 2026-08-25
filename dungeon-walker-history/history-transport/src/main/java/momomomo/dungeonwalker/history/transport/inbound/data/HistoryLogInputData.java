package momomomo.dungeonwalker.history.transport.inbound.data;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import momomomo.dungeonwalker.history.domain.inbound.data.HistoryLogInput;

import java.time.Instant;

@Getter
@ToString(includeFieldNames = false)
@SuperBuilder
public class HistoryLogInputData implements HistoryLogInput {

    private Instant timestamp;

}

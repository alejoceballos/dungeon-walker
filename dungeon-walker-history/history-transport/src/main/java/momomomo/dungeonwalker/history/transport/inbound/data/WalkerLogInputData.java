package momomomo.dungeonwalker.history.transport.inbound.data;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import momomomo.dungeonwalker.history.domain.inbound.data.CoordinatesLogInput;
import momomomo.dungeonwalker.history.domain.inbound.data.WalkerLogInput;

@Getter
@ToString(callSuper = true, includeFieldNames = false)
@SuperBuilder
public class WalkerLogInputData extends HistoryLogInputData implements WalkerLogInput {

    private String walkerId;

    private CoordinatesLogInput from;

    private CoordinatesLogInput to;

    private String reason;

}

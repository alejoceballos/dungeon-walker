package momomomo.dungeonwalker.history.domain.context;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CorrelationId {

    public static final ScopedValue<UUID> CORRELATION_ID = ScopedValue.newInstance();

}

package momomomo.dungeonwalker.history.domain.model.walker;

import momomomo.dungeonwalker.history.domain.model.map.Coordinates;

import java.time.Instant;

public interface WalkerHistory {

    Long getId();

    Instant getTimestamp();

    Coordinates getCoordinates();

}

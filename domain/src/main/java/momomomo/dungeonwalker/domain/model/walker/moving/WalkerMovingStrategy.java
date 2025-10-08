package momomomo.dungeonwalker.domain.model.walker.moving;

import momomomo.dungeonwalker.domain.model.coordinates.Coordinates;

import java.util.List;

public interface WalkerMovingStrategy {

    List<Coordinates> nextCoordinates(Coordinates previousCoordinates, Coordinates currentCoordinates);

}

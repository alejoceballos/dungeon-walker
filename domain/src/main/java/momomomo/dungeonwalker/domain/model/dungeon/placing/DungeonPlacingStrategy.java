package momomomo.dungeonwalker.domain.model.dungeon.placing;

import momomomo.dungeonwalker.domain.model.coordinates.Coordinates;
import momomomo.dungeonwalker.domain.model.dungeon.Dungeon;

public interface DungeonPlacingStrategy {

    Coordinates placingCoordinates(Dungeon dungeon);

}

package momomomo.dungeonwalker.engine.domain.model.dungeon.placing;

import momomomo.dungeonwalker.engine.domain.model.coordinates.Coordinates;
import momomomo.dungeonwalker.engine.domain.model.dungeon.Dungeon;

public interface DungeonPlacingStrategy {

    Coordinates placingCoordinates(Dungeon dungeon);

}

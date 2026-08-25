package momomomo.dungeonwalker.history.domain.model.map;

import lombok.NonNull;

public interface CoordinatesFactory {

    @NonNull Coordinates create(int x, int y);

}

package momomomo.dungeonwalker.engine.domain.model.walker;

import lombok.NonNull;
import momomomo.dungeonwalker.engine.domain.model.dungeon.Thing;

public record Walker(@NonNull String id) implements Thing {
}

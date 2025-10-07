package momomomo.dungeonwalker.domain.model.walker;

import lombok.NonNull;
import momomomo.dungeonwalker.domain.model.dungeon.Thing;

public record Walker(@NonNull String id) implements Thing {
}

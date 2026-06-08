package momomomo.dungeonwalker.engine.domain.model.walker.state;

import lombok.NonNull;
import momomomo.dungeonwalker.engine.domain.model.walker.Walker;

public class Moving extends WalkerState {

    public Moving(
            @NonNull final String id,
            @NonNull final String dungeonId) {
        super(id, dungeonId);
    }

    public static Moving of(@NonNull final Walker source) {
        final var target = new Moving(
                source.getId(),
                source.getDungeonId());

        copyCoordinates(source, target);

        return target;
    }

}

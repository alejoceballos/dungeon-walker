package momomomo.dungeonwalker.engine.domain.model.walker.state;

import lombok.NonNull;
import momomomo.dungeonwalker.engine.domain.model.walker.Walker;

public class Awake extends WalkerState {

    public Awake(
            @NonNull final String id,
            @NonNull final String dungeonId) {
        super(id, dungeonId);
    }

    public static Stopped of(@NonNull final Walker source) {
        final var target = new Stopped(
                source.getId(),
                source.getDungeonId());

        copyCoordinates(source, target);

        return target;
    }

}

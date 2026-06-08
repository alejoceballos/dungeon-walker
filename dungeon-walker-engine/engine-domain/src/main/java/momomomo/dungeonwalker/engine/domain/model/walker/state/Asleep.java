package momomomo.dungeonwalker.engine.domain.model.walker.state;

import lombok.NonNull;
import momomomo.dungeonwalker.engine.domain.model.walker.Walker;

public class Asleep extends WalkerState {

    public Asleep(@NonNull final String id) {
        super(id, null);
    }

    public static Asleep of(@NonNull final Walker source) {
        final var target = new Asleep(source.getId());

        target.setDungeonId(source.getDungeonId());
        copyCoordinates(source, target);

        return target;
    }

}

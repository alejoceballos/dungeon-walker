package momomomo.dungeonwalker.engine.domain.model.walker.state;

import lombok.NonNull;

public class Awake extends WalkerState {

    public Awake(
            @NonNull final String id,
            @NonNull final String dungeonId) {
        super(id, dungeonId);
    }

}

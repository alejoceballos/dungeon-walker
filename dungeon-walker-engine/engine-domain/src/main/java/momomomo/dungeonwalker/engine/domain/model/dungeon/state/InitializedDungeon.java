package momomomo.dungeonwalker.engine.domain.model.dungeon.state;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import momomomo.dungeonwalker.engine.domain.model.dungeon.Dungeon;

@NoArgsConstructor
public class InitializedDungeon extends DungeonState {

    public static InitializedDungeon of(@NonNull final Dungeon dungeon) {
        final var initialized = new InitializedDungeon();
        
        initialized.width = dungeon.getWidth();
        initialized.height = dungeon.getHeight();
        initialized.defaultSpawnLocation = dungeon.getDefaultSpawnLocation();
        initialized.cells.putAll(dungeon.getCells());

        return initialized;
    }

}

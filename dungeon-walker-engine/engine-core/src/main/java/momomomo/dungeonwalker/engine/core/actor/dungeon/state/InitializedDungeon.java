package momomomo.dungeonwalker.engine.core.actor.dungeon.state;

import lombok.NoArgsConstructor;
import momomomo.dungeonwalker.engine.domain.model.dungeon.Dungeon;

@NoArgsConstructor
public class InitializedDungeon extends DungeonState {

    public static InitializedDungeon of(final Dungeon dungeon) {
        final var initialized = new InitializedDungeon();
        
        initialized.width = dungeon.getWidth();
        initialized.height = dungeon.getHeight();
        initialized.defaultSpawnLocation = dungeon.getDefaultSpawnLocation();
        initialized.cells.putAll(dungeon.getCells());

        return initialized;
    }

}

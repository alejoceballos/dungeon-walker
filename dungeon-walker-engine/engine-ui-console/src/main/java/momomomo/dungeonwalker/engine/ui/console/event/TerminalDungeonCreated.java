package momomomo.dungeonwalker.engine.ui.console.event;

import lombok.NonNull;
import momomomo.dungeonwalker.engine.domain.event.DungeonEventListener;
import momomomo.dungeonwalker.engine.domain.event.DungeonUpdated;
import momomomo.dungeonwalker.engine.domain.model.coordinates.Coordinates;
import momomomo.dungeonwalker.engine.domain.model.dungeon.Wall;
import momomomo.dungeonwalker.engine.domain.model.walker.Walker;
import org.springframework.stereotype.Component;

@Component
public class TerminalDungeonCreated implements DungeonEventListener<DungeonUpdated> {

    @Override
    public void onEvent(@NonNull final DungeonUpdated event) {
        final var dungeon = event.dungeon();
        final var width = dungeon.getWidth();
        final var height = dungeon.getHeight();

        System.out.println();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                final String cell = switch (dungeon.at(Coordinates.of(x, y)).getOccupant()) {
                    case Wall _ -> "#";
                    case Walker walker -> Integer.valueOf(walker.id()).toString();
                    case null, default -> " ";
                };

                System.out.print(cell);
            }

            System.out.println();
        }
    }
}

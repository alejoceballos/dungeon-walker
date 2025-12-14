package momomomo.dungeonwalker.engine.startup.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.engine.domain.manager.DungeonManager;
import momomomo.dungeonwalker.engine.domain.model.dungeon.state.UninitializedDungeon;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class DisplayDungeonState {

    private static final String LABEL = "---> [SCHEDULER - Display Dungeon State]";

    public final DungeonManager dungeonManager;

    @Scheduled(initialDelay = 10L, fixedRate = 1L, timeUnit = TimeUnit.SECONDS)
    public void displayDungeon() {
        final var dungeon = dungeonManager.getDungeon();

        if (dungeon instanceof UninitializedDungeon) {
            log.debug("{} Dungeon state: {}", LABEL, dungeon);
            return;
        }

        dungeon.print();
    }

}

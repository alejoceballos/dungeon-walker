package momomomo.dungeonwalker.engine.startup.schedule;

import com.machinezoo.noexception.Exceptions;
import com.machinezoo.noexception.slf4j.ExceptionLogging;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.engine.core.actor.ClusterShardingManager;
import momomomo.dungeonwalker.engine.core.setup.DungeonIdentity;
import momomomo.dungeonwalker.engine.domain.model.dungeon.Dungeon;
import momomomo.dungeonwalker.engine.domain.model.dungeon.state.UninitializedDungeon;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "dungeonwalker.engine.schedule.display-dungeon-state.enabled", havingValue = "true")
public class DisplayDungeonState {

    private static final String LABEL = "---> [SCHEDULER - Display Dungeon State]";

    private final DungeonIdentity dungeonIdentity;
    private final ClusterShardingManager clusterManager;

    @Scheduled(initialDelay = 10L, fixedRate = 1L, timeUnit = TimeUnit.SECONDS)
    public void displayDungeon() {
        ExceptionLogging
                .log(log)
                .get(() -> this.getDungeon(1))
                .ifPresent(dungeon -> {
                    if (dungeon instanceof UninitializedDungeon) {
                        log.debug("{} Dungeon state: {}", LABEL, dungeon);
                        return;
                    }

                    log.debug("{}\n{}", LABEL, dungeon.print());
                });
    }

    public Dungeon getDungeon(final int level) {
        return Exceptions
                .wrap(GetDungeonException::new)
                .get(() -> clusterManager
                        .askForState(dungeonIdentity.id(level))
                        .toCompletableFuture()
                        .get()
                        .value());
    }

}

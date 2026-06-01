package momomomo.dungeonwalker.engine.startup.schedule;

import com.machinezoo.noexception.Exceptions;
import com.machinezoo.noexception.slf4j.ExceptionLogging;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.engine.core.actor.ClusterShardingManager;
import momomomo.dungeonwalker.engine.core.actor.dungeon.command.DungeonCommand;
import momomomo.dungeonwalker.engine.core.actor.dungeon.command.DungeonStateReply;
import momomomo.dungeonwalker.engine.core.actor.dungeon.command.DungeonStateRequest;
import momomomo.dungeonwalker.engine.core.setup.DungeonIdentity;
import momomomo.dungeonwalker.engine.domain.model.dungeon.Dungeon;
import momomomo.dungeonwalker.engine.domain.model.dungeon.state.UninitializedDungeon;
import org.apache.pekko.actor.typed.ActorSystem;
import org.apache.pekko.cluster.sharding.typed.javadsl.EntityRef;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

import static org.apache.pekko.actor.typed.javadsl.AskPattern.ask;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "dungeonwalker.engine.schedule.display-dungeon-state.enabled", havingValue = "true")
public class DisplayDungeonState {

    private static final String LABEL = "---> [SCHEDULER - Display Dungeon State]";

    private final ActorSystem<Void> actorSystem;
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
                .get(() -> askForState(dungeonIdentity.id(level))
                        .toCompletableFuture()
                        .get()
                        .value());
    }

    private CompletionStage<DungeonStateReply> askForState(final String dungeonId) {
        return ask(
                dungeonRef(dungeonId),
                DungeonStateRequest::new,
                Duration.ofSeconds(1L),
                actorSystem.scheduler());
    }

    private EntityRef<DungeonCommand> dungeonRef(final String dungeonId) {
        return clusterManager.dungeonRef(dungeonId);
    }

}

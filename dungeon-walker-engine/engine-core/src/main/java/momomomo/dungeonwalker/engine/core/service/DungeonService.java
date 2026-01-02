package momomomo.dungeonwalker.engine.core.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.engine.core.actor.ClusterShardingManager;
import momomomo.dungeonwalker.engine.core.actor.dungeon.command.DungeonCommand;
import momomomo.dungeonwalker.engine.core.actor.dungeon.command.DungeonStateReply;
import momomomo.dungeonwalker.engine.core.actor.dungeon.command.DungeonStateRequest;
import momomomo.dungeonwalker.engine.core.actor.dungeon.command.SetupDungeon;
import momomomo.dungeonwalker.engine.core.mapper.RawMapMapper;
import momomomo.dungeonwalker.engine.domain.model.dungeon.Dungeon;
import momomomo.dungeonwalker.engine.domain.model.dungeon.state.InitializedDungeon;
import org.apache.pekko.cluster.sharding.typed.javadsl.EntityRef;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.concurrent.ExecutionException;

import static java.lang.System.lineSeparator;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static org.apache.pekko.actor.typed.javadsl.AskPattern.ask;

@Slf4j
@Service
@RequiredArgsConstructor
public class DungeonService {

    private static final String LABEL = "---> [SERVICE - Dungeon]";
    private static final String FILE_PATH_PATTERN = File.separator + "dungeon" + File.separator + "%s.dgn";

    private final IdentityService identityService;
    private final RawMapMapper mapper;
    private final ClusterShardingManager cluster;

    public void setupLevel(final int level) {
        log.debug("{} Setting up level \"{}\"", LABEL, level);

        final var dungeonId = identityService.dungeonId(level);

        if (InitializedDungeon.class.equals(askForState(dungeonId).value().getClass())) {
            log.debug("{} Level \"{}\" already initialized", LABEL, level);
            return;
        }

        final var fileName = FILE_PATH_PATTERN.formatted(dungeonId);

        try (final var inputStream = getClass().getResourceAsStream(fileName)) {
            requireNonNull(inputStream);

            try (final var reader = new BufferedReader(new InputStreamReader(inputStream))) {
                final var rawMap = reader.lines().collect(joining(lineSeparator()));
                dungeonRef(dungeonId).tell(new SetupDungeon(mapper.map(level, rawMap)));
            }

        } catch (final IOException e) {
            throw new DungeonServiceException("Unable to set up dungeon level \"%s\".".formatted(level), e);
        }
    }

    public Dungeon getDungeon(final int level) {
        final var dungeonId = identityService.dungeonId(level);
        return askForState(dungeonId).value();
    }

    private DungeonStateReply askForState(final String dungeonId) {
        try {
            return ask(
                    dungeonRef(dungeonId),
                    DungeonStateRequest::new,
                    Duration.ofSeconds(1L),
                    cluster.getActorSystem().scheduler())
                    .toCompletableFuture()
                    .get();

        } catch (final InterruptedException | ExecutionException e) {
            throw new DungeonServiceException("Unable to ask dungeon for its state", e);
        }
    }

    private EntityRef<DungeonCommand> dungeonRef(final String dungeonId) {
        return cluster.getDungeonEntityRef(dungeonId);
    }

}

package momomomo.dungeonwalker.engine.core.setup;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.engine.core.actor.ClusterShardingManager;
import momomomo.dungeonwalker.engine.core.actor.dungeon.command.DungeonStateReply;
import momomomo.dungeonwalker.engine.core.actor.dungeon.command.DungeonStateRequest;
import momomomo.dungeonwalker.engine.core.actor.dungeon.command.SetupDungeon;
import momomomo.dungeonwalker.engine.domain.model.dungeon.state.InitializedDungeon;
import org.apache.pekko.actor.typed.ActorSystem;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

import static java.lang.System.lineSeparator;
import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.ObjectUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.pekko.actor.typed.javadsl.AskPattern.ask;

@Slf4j
@Service
@RequiredArgsConstructor
public class DungeonSetup {

    private static final String LABEL = "---> [SERVICE - Dungeon]";

    private static final String DUNGEONS_EXTENSION = ".dgn";
    private static final String DUNGEONS_FOLDER = "dungeons";
    private static final String FILE_PATH_PATTERN = DUNGEONS_FOLDER + File.separator + "%s" + DUNGEONS_EXTENSION;

    private final ActorSystem<Void> actorSystem;
    private final DungeonIdentity dungeonIdentity;
    private final RawDungeonMapper mapper;
    private final ClusterShardingManager clusterManager;

    public List<Integer> dungeonLevels() {
        final var resource = getClass().getClassLoader().getResource(DUNGEONS_FOLDER);

        if (isNull(resource)) {
            throw new NoDungeonFilesException("Dungeons folder not found in resources");
        }

        final var dungeonsDir = new File(resource.getPath());

        if (!dungeonsDir.exists() || !dungeonsDir.isDirectory()) {
            throw new NoDungeonFilesException("Dungeons folder not found or is not a directory");
        }

        final var dungeonFiles = dungeonsDir.list();

        if (isEmpty(dungeonFiles)) {
            throw new NoDungeonFilesException("No files found in dungeons folder");
        }

        return Stream
                .of(dungeonFiles)
                .filter(fileName -> fileName.endsWith(DUNGEONS_EXTENSION))
                .map(fileName -> fileName.replace(DUNGEONS_EXTENSION, EMPTY))
                .map(dungeonIdentity::level)
                .toList();
    }

    public void setupLevel(final int level) {
        log.debug("{} Setting up level \"{}\"", LABEL, level);

        final var dungeonId = dungeonIdentity.id(level);

        askForState(dungeonId).thenAccept(state -> {
            if (InitializedDungeon.class.equals(state.value().getClass())) {
                log.debug("{} Level \"{}\" already initialized", LABEL, level);
                return;
            }

            try (final var inputStream = getClass().getClassLoader().getResourceAsStream(dungeonFilePath(dungeonId))) {
                try (final var reader = new BufferedReader(new InputStreamReader(requireNonNull(inputStream)))) {
                    final var rawMap = reader.lines().collect(joining(lineSeparator()));
                    clusterManager.tellDungeon(dungeonId, new SetupDungeon(mapper.map(level, rawMap)));
                }

            } catch (final IOException ex) {
                throw new DungeonSetupException("Unable to set up dungeon level \"%s\".".formatted(level), ex);
            }
        });
    }

    private CompletionStage<DungeonStateReply> askForState(final String dungeonId) {
        return ask(
                clusterManager.dungeonRef(dungeonId),
                DungeonStateRequest::new,
                Duration.ofSeconds(1L),
                actorSystem.scheduler());
    }

    private static @NonNull String dungeonFilePath(final String dungeonId) {
        return FILE_PATH_PATTERN.formatted(dungeonId);
    }
    
}

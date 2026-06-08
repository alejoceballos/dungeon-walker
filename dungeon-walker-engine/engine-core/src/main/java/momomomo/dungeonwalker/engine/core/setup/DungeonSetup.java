package momomomo.dungeonwalker.engine.core.setup;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.engine.core.actor.ClusterShardingManager;
import momomomo.dungeonwalker.engine.core.actor.dungeon.command.from.setup.SetupDungeon;
import momomomo.dungeonwalker.engine.core.setup.loader.DngFileLoader;
import momomomo.dungeonwalker.engine.domain.model.dungeon.state.InitializedDungeon;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeoutException;

import static java.lang.System.lineSeparator;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.Executors.newScheduledThreadPool;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.StringUtils.EMPTY;

@Slf4j
@Component
@RequiredArgsConstructor
public class DungeonSetup {

    private static final String LABEL = "---> [DUNGEON SETUP]";

    private static final String DUNGEONS_EXTENSION = ".dgn";
    private static final String DUNGEONS_FOLDER = "dungeons";
    private static final String FILE_PATH_PATTERN = DUNGEONS_FOLDER + File.separator + "%s" + DUNGEONS_EXTENSION;

    private final DungeonIdentity dungeonIdentity;
    private final RawDungeonMapper mapper;
    private final ClusterShardingManager clusterManager;
    private final DngFileLoader fileLoader;
    private final Duration dungeonKeepAliveInterval;

    private ScheduledExecutorService keepAliveExecutor;
    private List<ScheduledFuture> keepAliveTimers;

    @PostConstruct
    public void postConstruct() {
        log.debug("{} Initializing dungeon", LABEL);
        final var dungeonsId = dungeonLevels()
                .stream()
                .map(this::setupLevel)
                .toList();

        keepAliveExecutor = newScheduledThreadPool(dungeonsId.size());

        keepAliveTimers = dungeonsId
                .stream()
                .map(this::startKeepAliveTimer)
                .toList();
    }

    @PreDestroy
    public void preDestroy() {
        if (keepAliveExecutor != null) {
            keepAliveExecutor.shutdown();
        }
    }

    private List<Integer> dungeonLevels() {
        log.debug("{} Getting dungeon levels", LABEL);

        final var levels = fileLoader
                .listFiles()
                .filter(fileName -> fileName.endsWith(DUNGEONS_EXTENSION))
                .map(fileName -> fileName.replace(DUNGEONS_EXTENSION, EMPTY))
                .map(dungeonIdentity::level)
                .toList();

        log.debug("{} Found {} level(s)", LABEL, levels.size());

        return levels;
    }

    private String setupLevel(final int level) {
        log.debug("{} Setting up level \"{}\"", LABEL, level);

        final var dungeonId = dungeonIdentity.id(level);

        log.debug("{} Checking for existence of dungeon \"{}\"", LABEL, dungeonId);

        try {
            clusterManager
                    .askForState(dungeonId)
                    .thenAccept(state -> {
                        if (InitializedDungeon.class.equals(state.value().getClass())) {
                            log.debug("{} Level \"{}\" already initialized", LABEL, level);
                            return;
                        }

                        log.debug("{} Dungeon \"{}\" does not exist. Initializing...", LABEL, dungeonId);

                        try (final var inputStream = getClass().getClassLoader().getResourceAsStream(dungeonFilePath(dungeonId))) {
                            log.debug("{} Resource for dungeon \"{}\" retrieved as input stream", LABEL, dungeonId);

                            try (final var reader = new BufferedReader(new InputStreamReader(requireNonNull(inputStream)))) {
                                log.debug("{} Input stream for \"{}\" buffered. Reading lines...", LABEL, dungeonId);

                                final var rawMap = reader.lines().collect(joining(lineSeparator()));

                                log.info("{} Dungeon \"{}\" raw map:\n{}", LABEL, dungeonId, rawMap);
                                log.info("{} Setting up the Dungeon \"{}\" map object", LABEL, dungeonId);

                                clusterManager.tellDungeon(dungeonId, new SetupDungeon(mapper.map(level, rawMap)));
                            }

                        } catch (final IOException ex) {
                            throw new DungeonSetupException("Unable to set up dungeon level \"%s\".".formatted(level), ex);
                        }
                    })
                    .exceptionally(ex -> {
                        log.error("{} Failed to set up dungeon level \"{}\". Error: {}", LABEL, level, ex.getMessage());
                        return null;
                    })
                    .toCompletableFuture()
                    .get(5, SECONDS);

        } catch (final ExecutionException | TimeoutException ex) {
            throw new DungeonSetupException(ex);

        } catch (final InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new DungeonSetupException(ex);
        }

        return dungeonId;
    }

    private static @NonNull String dungeonFilePath(final String dungeonId) {
        return FILE_PATH_PATTERN.formatted(dungeonId);
    }

    private ScheduledFuture startKeepAliveTimer(final String dungeonId) {
        log.debug("{} Initializing \"keep alive\" timer for dungeon \"{}\"", LABEL, dungeonId);

        log.debug("{} Scheduling \"{}\" \"keep alive\" at every {} seconds", LABEL, dungeonId, dungeonKeepAliveInterval.getSeconds());

        return keepAliveExecutor.scheduleAtFixedRate(
                () -> {
                    log.debug("{} ping to \"{}\"", LABEL, dungeonId);
                    clusterManager
                            .askForKeepALive(dungeonId)
                            .thenAccept(_ -> log.debug("{} pong from \"{}\"", LABEL, dungeonId));
                },
                dungeonKeepAliveInterval.getSeconds(),
                dungeonKeepAliveInterval.getSeconds(),
                SECONDS);
    }

}

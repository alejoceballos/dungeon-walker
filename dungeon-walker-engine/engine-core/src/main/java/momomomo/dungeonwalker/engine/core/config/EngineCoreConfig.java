package momomomo.dungeonwalker.engine.core.config;

import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.commons.UuidGenerator;
import momomomo.dungeonwalker.engine.core.setup.NoDungeonFilesException;
import momomomo.dungeonwalker.engine.core.setup.loader.DngFileLoader;
import momomomo.dungeonwalker.engine.core.setup.loader.JarDngFileLoader;
import momomomo.dungeonwalker.engine.core.setup.loader.SystemDngFileLoader;
import momomomo.dungeonwalker.engine.domain.model.dungeon.placing.DungeonPlacingStrategy;
import momomomo.dungeonwalker.engine.domain.model.dungeon.placing.SpiralStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static java.util.Objects.isNull;

@Slf4j
@Configuration
public class EngineCoreConfig {

    private static final String LABEL = "---> [ENGINE CONFIG]";

    private static final String DUNGEONS_FOLDER = "dungeons";
    private static final Duration SIX_MONTHS = Duration.of(200, ChronoUnit.DAYS);

    @Bean
    public UuidGenerator uuidGenerator() {
        return new UuidGenerator();
    }

    @Bean
    public DungeonPlacingStrategy placingStrategy() {
        return new SpiralStrategy();
    }

    @Bean
    public Duration dungeonHeartbeatInterval(
            @Value("${dungeonwalker.engine.heartbeat.enabled}") final boolean enabled,
            @Value("${dungeonwalker.engine.heartbeat.interval.value}") final long value,
            @Value("${dungeonwalker.engine.heartbeat.interval.unit}") final String unit
    ) {
        return enabled ? Duration.of(value, ChronoUnit.valueOf(unit)) : SIX_MONTHS;
    }

    @Bean
    public Duration dungeonKeepAliveInterval(
            @Value("${dungeonwalker.engine.dungeon.keep-alive.enabled}") final boolean enabled,
            @Value("${dungeonwalker.engine.dungeon.keep-alive.value}") final long value,
            @Value("${dungeonwalker.engine.dungeon.keep-alive.unit}") final String unit
    ) {
        return enabled ? Duration.of(value, ChronoUnit.valueOf(unit)) : SIX_MONTHS;
    }

    @Bean
    public DngFileLoader dngFileLoader() {
        log.debug("{} Defining dungeon files loader", LABEL);

        final var resourcePath = DUNGEONS_FOLDER + File.separator;
        final var resourceUrl = getClassLoader().getResource(resourcePath);

        log.debug("{} Resources loaded: \"{}\"", LABEL, resourceUrl);

        if (isNull(resourceUrl)) {
            throw new NoDungeonFilesException("Dungeons folder not found in resources");
        }

        if (resourceUrl.getProtocol().equals("jar")) {
            return new JarDngFileLoader(resourceUrl, resourcePath);
        }

        return new SystemDngFileLoader(resourceUrl);
    }

    private ClassLoader getClassLoader() {
        var classLoader = Thread.currentThread().getContextClassLoader();

        if (isNull(classLoader)) {
            classLoader = this.getClass().getClassLoader();
        }

        return classLoader;
    }

}

package momomomo.dungeonwalker.engine.core.config;

import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.engine.domain.model.dungeon.placing.DungeonPlacingStrategy;
import momomomo.dungeonwalker.engine.domain.model.dungeon.placing.SpiralStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Slf4j
@Configuration
public class EngineConfig {

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
        return enabled ?
                Duration.of(value, ChronoUnit.valueOf(unit)) :
                Duration.of(1, ChronoUnit.CENTURIES);
    }

}

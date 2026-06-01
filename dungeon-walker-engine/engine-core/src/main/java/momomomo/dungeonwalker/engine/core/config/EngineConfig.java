package momomomo.dungeonwalker.engine.core.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.engine.core.setup.DungeonSetup;
import momomomo.dungeonwalker.engine.domain.model.dungeon.placing.DungeonPlacingStrategy;
import momomomo.dungeonwalker.engine.domain.model.dungeon.placing.SpiralStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class EngineConfig {

    private static final String LABEL = "---> [ENGINE CONFIG]";

    private final DungeonSetup dungeonSetup;

    @Bean
    public DungeonPlacingStrategy placingStrategy() {
        return new SpiralStrategy();
    }

    @PostConstruct
    void init() {
        log.debug("{} Initializing dungeon", LABEL);
        dungeonSetup
                .dungeonLevels()
                .forEach(dungeonSetup::setupLevel);
    }

}

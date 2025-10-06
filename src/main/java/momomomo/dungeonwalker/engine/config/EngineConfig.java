package momomomo.dungeonwalker.engine.config;

import momomomo.dungeonwalker.domain.model.dungeon.placing.DungeonPlacingStrategy;
import momomomo.dungeonwalker.domain.model.dungeon.placing.SpiralStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EngineConfig {

    @Bean
    public DungeonPlacingStrategy placingStrategy() {
        return new SpiralStrategy();
    }

}

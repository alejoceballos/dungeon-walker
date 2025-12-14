package momomomo.dungeonwalker.engine.core.config;

import momomomo.dungeonwalker.engine.domain.model.dungeon.placing.DungeonPlacingStrategy;
import momomomo.dungeonwalker.engine.domain.model.dungeon.placing.SpiralStrategy;
import momomomo.dungeonwalker.engine.domain.model.walker.moving.SameDirectionOrRandomOtherwise;
import momomomo.dungeonwalker.engine.domain.model.walker.moving.WalkerMovementStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EngineConfig {

    @Bean
    public DungeonPlacingStrategy placingStrategy() {
        return new SpiralStrategy();
    }

    @Bean
    public WalkerMovementStrategy movingStrategy() {
        return new SameDirectionOrRandomOtherwise();
    }

}

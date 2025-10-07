package momomomo.dungeonwalker.engine.config;

import akka.actor.typed.ActorSystem;
import momomomo.dungeonwalker.domain.model.dungeon.placing.DungeonPlacingStrategy;
import momomomo.dungeonwalker.domain.model.walker.moving.WalkerMovingStrategy;
import momomomo.dungeonwalker.engine.guardian.EngineActor;
import momomomo.dungeonwalker.engine.guardian.command.EngineCommand;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AkkaConfig {

    @Bean
    public ActorSystem<EngineCommand> guardian(
            final DungeonPlacingStrategy placingStrategy,
            final WalkerMovingStrategy movingStrategy) {
        return ActorSystem.create(EngineActor.create(placingStrategy, movingStrategy), "dungeon-engine");
    }

}

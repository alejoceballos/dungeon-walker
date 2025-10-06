package momomomo.dungeonwalker.engine.config;

import akka.actor.typed.ActorSystem;
import momomomo.dungeonwalker.domain.model.dungeon.placing.DungeonPlacingStrategy;
import momomomo.dungeonwalker.engine.guardian.EngineActor;
import momomomo.dungeonwalker.engine.guardian.command.EngineCommand;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AkkaConfig {

    @Bean
    public ActorSystem<EngineCommand> guardian(final DungeonPlacingStrategy placingStrategy) {
        return ActorSystem.create(EngineActor.create(placingStrategy), "dungeon-engine");
    }

}

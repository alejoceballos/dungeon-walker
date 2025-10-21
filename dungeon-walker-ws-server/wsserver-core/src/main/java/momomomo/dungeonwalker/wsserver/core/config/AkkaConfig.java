package momomomo.dungeonwalker.wsserver.core.config;

import akka.actor.typed.ActorSystem;
import momomomo.dungeonwalker.commons.DateTimeManager;
import momomomo.dungeonwalker.wsserver.core.guardian.GuardianActor;
import momomomo.dungeonwalker.wsserver.core.guardian.command.GuardianCommand;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AkkaConfig {

    @Bean
    public ActorSystem<GuardianCommand> guardian(
            final DateTimeManager dateTimeManager,
            final HeartbeatConfig heartbeatConfig) {
        return ActorSystem.create(GuardianActor.create(dateTimeManager, heartbeatConfig), "connection-guardian");
    }

}

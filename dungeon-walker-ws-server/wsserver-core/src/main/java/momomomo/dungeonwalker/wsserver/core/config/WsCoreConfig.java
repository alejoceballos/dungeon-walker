package momomomo.dungeonwalker.wsserver.core.config;

import momomomo.dungeonwalker.commons.DateTimeManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WsCoreConfig {

    @Bean
    public DateTimeManager dateTimeManager() {
        return new DateTimeManager();
    }

}

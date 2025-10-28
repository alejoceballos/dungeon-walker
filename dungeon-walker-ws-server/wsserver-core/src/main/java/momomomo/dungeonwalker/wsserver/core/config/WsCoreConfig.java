package momomomo.dungeonwalker.wsserver.core.config;

import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.commons.DateTimeManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class WsCoreConfig {

    @Bean
    public DateTimeManager dateTimeManager() {
        log.info("---> [WS CORE - Config] 'dateTimeManager' bean created");
        return new DateTimeManager();
    }

}

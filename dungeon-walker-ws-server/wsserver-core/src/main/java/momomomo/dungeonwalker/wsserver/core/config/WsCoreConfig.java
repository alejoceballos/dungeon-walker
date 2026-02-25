package momomomo.dungeonwalker.wsserver.core.config;

import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.commons.DateTimeManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class WsCoreConfig {

    private static final String LABEL = "---> [WS CORE - Config]";

    @Bean
    public DateTimeManager dateTimeManager() {
        log.info("{} 'dateTimeManager' bean created", LABEL);
        return new DateTimeManager();
    }

}

package momomomo.dungeonwalker.wsserver.transport.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class WsTransportConfig {

    @Bean
    public ObjectMapper jsonMapper() {
        log.info("---> [WS TRANSPORT - Config] 'jsonMapper' bean created");
        return new ObjectMapper();
    }

}

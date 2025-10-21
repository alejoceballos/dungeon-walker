package momomomo.dungeonwalker.wsserver.transport.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WsTransportConfig {

    @Bean
    public ObjectMapper jsonMapper() {
        return new ObjectMapper();
    }

}

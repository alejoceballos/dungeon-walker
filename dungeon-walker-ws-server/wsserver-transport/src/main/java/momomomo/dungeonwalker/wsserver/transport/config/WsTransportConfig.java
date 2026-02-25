package momomomo.dungeonwalker.wsserver.transport.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class WsTransportConfig {

    private static final String LABEL = "---> [WS TRANSPORT - Config]";

    @Bean
    public JavaTimeModule javaTimeModule() {
        return new JavaTimeModule();
    }

    @Bean
    public ObjectMapper jsonMapper(final JavaTimeModule javaTimeModule) {
        log.info("{} 'jsonMapper' bean created", LABEL);
        return new ObjectMapper().registerModule(javaTimeModule);
    }

}

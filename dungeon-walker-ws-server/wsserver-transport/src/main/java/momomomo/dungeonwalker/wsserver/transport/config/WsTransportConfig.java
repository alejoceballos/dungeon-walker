package momomomo.dungeonwalker.wsserver.transport.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class WsTransportConfig {

    @Bean
    public JavaTimeModule javaTimeModule() {
        return new JavaTimeModule();
    }

    @Bean
    public ObjectMapper jsonMapper(final JavaTimeModule javaTimeModule) {
        log.info("---> [WS TRANSPORT - Config] 'jsonMapper' bean created");
        return new ObjectMapper().registerModule(javaTimeModule);
    }

}

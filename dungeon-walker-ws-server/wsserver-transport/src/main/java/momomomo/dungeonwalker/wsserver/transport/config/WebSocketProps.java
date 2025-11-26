package momomomo.dungeonwalker.wsserver.transport.config;

import lombok.Getter;
import lombok.Setter;
import momomomo.dungeonwalker.spring.commons.YamlPropertySourceFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "websocket")
@PropertySource(value = "classpath:application-transport.yml", factory = YamlPropertySourceFactory.class)
public class WebSocketProps {

    private String endpoint;
    private String allowedOriginPatterns;

}

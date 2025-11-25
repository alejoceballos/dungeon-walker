package momomomo.dungeonwalker.engine.transport.config;

import momomomo.dungeonwalker.spring.commons.YamlPropertySourceFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:application-transport.yml", factory = YamlPropertySourceFactory.class)
public class EngineTransportConfig {
}

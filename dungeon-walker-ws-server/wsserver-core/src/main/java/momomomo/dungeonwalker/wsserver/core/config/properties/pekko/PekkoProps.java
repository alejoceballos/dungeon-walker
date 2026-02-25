package momomomo.dungeonwalker.wsserver.core.config.properties.pekko;

import lombok.Getter;
import lombok.Setter;
import momomomo.dungeonwalker.spring.commons.YamlPropertySourceFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "pekko")
@PropertySource(value = "classpath:application-core.yml", factory = YamlPropertySourceFactory.class)
public class PekkoProps {

    private RemoteProps remote;
    private ClusterProps cluster;

}

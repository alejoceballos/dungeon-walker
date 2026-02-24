package momomomo.dungeonwalker.engine.core.config.properties.pekko;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "pekko")
public class PekkoProps {

    private PersistenceProps persistence;
    private RemoteProps remote;
    private ClusterProps cluster;

}

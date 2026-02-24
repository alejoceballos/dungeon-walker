package momomomo.dungeonwalker.engine.core.config.properties.pekko;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class R2dbcProps {

    private String dialect;
    private ConnectionFactoryProps connectionFactory;

}

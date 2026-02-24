package momomomo.dungeonwalker.engine.core.config.properties.pekko;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConnectionFactoryProps {

    private String driver;
    private String host;
    private String port;
    private String database;
    private String user;
    private String password;

}

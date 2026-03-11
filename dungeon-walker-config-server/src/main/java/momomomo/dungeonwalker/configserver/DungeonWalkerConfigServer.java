package momomomo.dungeonwalker.configserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class DungeonWalkerConfigServer {

    static void main(final String... args) {
        SpringApplication.run(DungeonWalkerConfigServer.class, args);
    }

}

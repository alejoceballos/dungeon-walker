package momomomo.dungeonwalker.wsserver.startup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "momomomo.dungeonwalker.wsserver")
public class DungeonWalkerWsServer {

    static void main(String[] args) {
        SpringApplication.run(DungeonWalkerWsServer.class, args);
    }

}

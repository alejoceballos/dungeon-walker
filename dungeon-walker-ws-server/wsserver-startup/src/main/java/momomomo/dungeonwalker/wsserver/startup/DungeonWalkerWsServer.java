package momomomo.dungeonwalker.wsserver.startup;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication(scanBasePackages = "momomomo.dungeonwalker.wsserver")
public class DungeonWalkerWsServer {

    static void main(String[] args) {
        log.info("Starting Dungeon Walker Websocket Server...");

        SpringApplication.run(DungeonWalkerWsServer.class, args);
        
        log.info("Dungeon Walker Websocket Server started successfully.");
    }

}

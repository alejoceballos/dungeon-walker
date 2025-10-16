package momomomo.dungeonwalker.engine.startup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "momomomo.dungeonwalker.engine")
public class DungeonWalkerEngine {

    static void main(String[] args) {
        SpringApplication.run(DungeonWalkerEngine.class, args);
    }

}

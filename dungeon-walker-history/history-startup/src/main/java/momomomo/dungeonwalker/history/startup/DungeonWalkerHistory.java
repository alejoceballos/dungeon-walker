package momomomo.dungeonwalker.history.startup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "momomomo.dungeonwalker.history")
public class DungeonWalkerHistory {
    static void main(final String... args) {
        SpringApplication.run(DungeonWalkerHistory.class, args);
    }
}

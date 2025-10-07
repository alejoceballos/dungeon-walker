package momomomo.dungeonwalker.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"momomomo.dungeonwalker"})
public class DungeonWalker {

    static void main(String[] args) {
        SpringApplication.run(DungeonWalker.class, args);
    }

}

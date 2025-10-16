package momomomo.dungeonwalker.engine.core.config;

import momomomo.dungeonwalker.engine.domain.model.coordinates.Coordinates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RawMapConfig {

    private static final String MAP = """
            W,W,W,W,W,W,W,W,W,W
            W,,,,,,,,,W
            W,,,,,,,,,W
            W,,,,,,,,,W
            W,,,,,,,,,W
            W,,,,,,,,,W
            W,,,,,,,,,W
            W,,,,,,,,,W
            W,,,,,,,,,W
            W,W,W,W,W,W,W,W,W,W
            """;

    @Bean("rawMap")
    public String loadMap() {
        return MAP;
    }

    @Bean
    public Coordinates defaultSpawnLocation() {
        return Coordinates.of(5, 5);
    }

}

package momomomo.dungeonwalker.history.transport.config;

import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "momomomo.dungeonwalker.history.transport")
@EntityScan(basePackages = "momomomo.dungeonwalker.history.transport")
public class JpaConfig {
}

package momomomo.dungeonwalker.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator routeLocator(final RouteLocatorBuilder builder) {
        return builder
                .routes()
                .route(ps -> ps
                        .path("/ws-server/**")
                        .uri("lb:ws://DUNGEON-WALKER-WS-SERVER"))
                .build();
    }

}

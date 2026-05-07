package momomomo.dungeonwalker.gateway.config;

import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import reactor.core.publisher.Mono;

import static java.util.Objects.requireNonNull;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(final ServerHttpSecurity http) {
        return http
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/ws-server/ws-endpoint/**").permitAll()
                        .anyExchange().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(Customizer.withDefaults())
                        .bearerTokenConverter(extractBearerAuthenticationToken()))
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .build();
    }

    private @NonNull ServerAuthenticationConverter extractBearerAuthenticationToken() {
        return exchange -> {
            final var request = exchange.getRequest();

            return isWebSocketEndpoint(request) && hasAuthentication(request) ?
                    Mono.empty() :
                    Mono.just(new BearerTokenAuthenticationToken(extractToken(request)));
        };
    }

    private boolean isWebSocketEndpoint(final ServerHttpRequest request) {
        return "/ws-server/ws-endpoint".equalsIgnoreCase(request.getURI().getPath());
    }

    private boolean hasAuthentication(final ServerHttpRequest request) {
        final var authHeader = getAuthorizationHeader(request);
        return authHeader != null && authHeader.startsWith("Bearer ");
    }

    private String getAuthorizationHeader(final ServerHttpRequest request) {
        return request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
    }

    private String extractToken(final ServerHttpRequest request) {
        return hasAuthentication(request) ?
                requireNonNull(getAuthorizationHeader(request)).replace("Bearer ", "") :
                "INVALID";
    }

}

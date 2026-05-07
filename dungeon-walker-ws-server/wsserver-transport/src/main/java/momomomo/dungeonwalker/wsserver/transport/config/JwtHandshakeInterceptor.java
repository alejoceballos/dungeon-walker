package momomomo.dungeonwalker.wsserver.transport.config;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

@Slf4j
@Component
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final String endpoint;
    private final JwtDecoder jwtDecoder;

    public JwtHandshakeInterceptor(
            @Value("${websocket.endpoint}") final String endpoint,
            @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}") final String jwkSetUri
    ) {
        this.endpoint = endpoint;
        this.jwtDecoder = NimbusJwtDecoder
                .withJwkSetUri(jwkSetUri)
                .build();
    }

    @Override
    public boolean beforeHandshake(
            @NonNull final ServerHttpRequest request,
            @NonNull final ServerHttpResponse response,
            @NonNull final WebSocketHandler wsHandler,
            @NonNull final Map<String, Object> attributes) {
        if (isNotWebSocketEndpoint(request)) {
            return true;
        }

        if (hasNoAuthentication(request)) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }

        try {
            final var jwt = jwtDecoder.decode(extractToken(request));
            attributes.put("jwt", jwt);
            return true;

        } catch (final Exception _) {
            response.setStatusCode(HttpStatus.BAD_REQUEST);
            return false;
        }
    }

    @Override
    public void afterHandshake(
            @NonNull final ServerHttpRequest req,
            @NonNull final ServerHttpResponse res,
            @NonNull final WebSocketHandler handler,
            final Exception ex) {
        log.debug("After handshake");
    }

    private boolean isNotWebSocketEndpoint(final ServerHttpRequest request) {
        return !endpoint.equalsIgnoreCase(request.getURI().getPath());
    }

    private boolean hasNoAuthentication(final ServerHttpRequest request) {
        final var authHeader = getAuthorizationHeader(request);
        return isNull(authHeader) || !authHeader.startsWith("Bearer ");
    }

    private String getAuthorizationHeader(final ServerHttpRequest request) {
        return request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
    }

    private String extractToken(final ServerHttpRequest request) {
        return requireNonNull(getAuthorizationHeader(request))
                .replace("Bearer ", "");
    }

}

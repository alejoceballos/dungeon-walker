package momomomo.dungeonwalker.wsserver.transport.auth;

import lombok.NonNull;
import momomomo.dungeonwalker.wsserver.domain.auth.Authorizer;
import momomomo.dungeonwalker.wsserver.domain.auth.WsServerExpiredAuthorizationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

import static java.util.Objects.isNull;

@Component
public class JwtAuthorizer implements Authorizer {

    private final JwtDecoder jwtDecoder;

    public JwtAuthorizer(@Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}") final String jwkSetUri) {
        jwtDecoder = NimbusJwtDecoder
                .withJwkSetUri(jwkSetUri)
                .build();
    }

    @Override
    public String authorize(@NonNull final String credentials) {
        try {
            final var jwt = jwtDecoder.decode(extractToken(credentials));

            if (isNull(jwt.getExpiresAt())) {
                throw new WsServerExpiredAuthorizationException("Cannot authorize tokens without expiration");
            }

            if (Optional.ofNullable(jwt.getExpiresAt()).orElse(Instant.now()).isBefore(Instant.now())) {
                throw new WsServerExpiredAuthorizationException("Authorization expired at " + jwt.getExpiresAt());
            }

            return jwt.getClaim("preferred_username");

        } catch (final JwtException ex) {
            throw new WsServerExpiredAuthorizationException(ex);
        }
    }

    private String extractToken(final String credentials) {
        return credentials.replaceFirst("Bearer ", "");
    }

}

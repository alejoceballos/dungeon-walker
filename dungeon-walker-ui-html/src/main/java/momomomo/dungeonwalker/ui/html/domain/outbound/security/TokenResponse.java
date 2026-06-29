package momomomo.dungeonwalker.ui.html.domain.outbound.security;

import com.fasterxml.jackson.annotation.JsonAlias;

public record TokenResponse(
        @JsonAlias("access_token") String accessToken,
        @JsonAlias("expires_in") int expiresIn,
        @JsonAlias("refresh_expires_in") int refreshExpiresIn,
        @JsonAlias("refresh_token") String refreshToken,
        @JsonAlias("token_type") String tokenType,
        @JsonAlias("not-before-policy") int notBeforePolicy,
        @JsonAlias("session_state") String sessionState,
        @JsonAlias("scope") String scope
) {
}

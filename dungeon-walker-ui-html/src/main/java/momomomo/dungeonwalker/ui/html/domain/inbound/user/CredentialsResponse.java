package momomomo.dungeonwalker.ui.html.domain.inbound.user;

import lombok.NonNull;

public record CredentialsResponse(
        String token,
        String errorMessage
) {

    public static CredentialsResponse success(@NonNull final String token) {
        return new CredentialsResponse(token, null);
    }

    public static CredentialsResponse error(@NonNull final String error) {
        return new CredentialsResponse(null, error);
    }

}

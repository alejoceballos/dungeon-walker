package momomomo.dungeonwalker.ui.html.domain.inbound.user;

import jakarta.validation.constraints.NotEmpty;

public record CredentialsRequest(
        @NotEmpty String username,
        @NotEmpty String password) {
}

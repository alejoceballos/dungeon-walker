package momomomo.dungeonwalker.ui.html.domain.outbound.security;

import lombok.NonNull;

public interface SecurityGateway {

    TokenResponse requestToken(@NonNull String username, @NonNull String password);

}

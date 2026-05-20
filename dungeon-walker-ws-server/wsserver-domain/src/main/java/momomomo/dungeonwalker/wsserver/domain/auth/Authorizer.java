package momomomo.dungeonwalker.wsserver.domain.auth;

import lombok.NonNull;

public interface Authorizer {

    String authorize(@NonNull String credentials);

}

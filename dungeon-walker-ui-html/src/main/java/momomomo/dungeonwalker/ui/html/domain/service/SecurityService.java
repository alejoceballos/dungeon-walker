package momomomo.dungeonwalker.ui.html.domain.service;

import lombok.NonNull;

public interface SecurityService {

    String requestToken(@NonNull String userName, @NonNull String password);

}

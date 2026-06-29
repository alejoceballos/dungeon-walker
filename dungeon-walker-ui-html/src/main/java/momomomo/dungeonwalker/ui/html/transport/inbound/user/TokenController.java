package momomomo.dungeonwalker.ui.html.transport.inbound.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.ui.html.domain.inbound.user.CredentialsRequest;
import momomomo.dungeonwalker.ui.html.domain.inbound.user.CredentialsResponse;
import momomomo.dungeonwalker.ui.html.domain.outbound.security.SecurityGatewayException;
import momomomo.dungeonwalker.ui.html.domain.service.SecurityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/token")
@RequiredArgsConstructor
public class TokenController {

    private final SecurityService security;

    @PostMapping("/credentials")
    public ResponseEntity<CredentialsResponse> retrieveToken(
            @Valid @RequestBody final CredentialsRequest credentials
    ) {
        log.debug("Retrieving token for user: {}/{}", credentials.username(), credentials.password());

        try {
            final var token = security.requestToken(credentials.username(), credentials.password());

            log.debug("Token retrieved for user: {}/{} = {}", credentials.username(), credentials.password(), token);

            return ResponseEntity.ok(CredentialsResponse.success(token));

        } catch (final SecurityGatewayException sgex) {
            log.error("Error occurred while retrieving token for user: {}/{}. Error: {}",
                    credentials.username(), credentials.password(), sgex.getMessage());

            return switch (sgex.getType()) {
                case CREDENTIALS_CLIENT_ERROR -> ResponseEntity
                        .badRequest()
                        .body(CredentialsResponse.error(sgex.getMessage()));
                case CREDENTIALS_UNMAPPED_ERROR, CONNECTION_ERROR, CONNECTION_FATAL_ERROR -> ResponseEntity
                        .status(HttpStatus.BAD_GATEWAY)
                        .body(CredentialsResponse.error(sgex.getMessage()));
                default -> ResponseEntity
                        .internalServerError()
                        .body(CredentialsResponse.error(sgex.getMessage()));
            };
        }
    }

}

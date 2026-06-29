package momomomo.dungeonwalker.ui.html.transport.outbound.security;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.ui.html.domain.outbound.security.SecurityGateway;
import momomomo.dungeonwalker.ui.html.domain.outbound.security.SecurityGatewayException;
import momomomo.dungeonwalker.ui.html.domain.outbound.security.TokenResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.ObjectMapper;

import java.util.Collections;

import static momomomo.dungeonwalker.ui.html.domain.outbound.security.SecurityGatewayException.Type.CREDENTIALS_CLIENT_ERROR;
import static momomomo.dungeonwalker.ui.html.domain.outbound.security.SecurityGatewayException.Type.CREDENTIALS_FATAL_ERROR;
import static momomomo.dungeonwalker.ui.html.domain.outbound.security.SecurityGatewayException.Type.CREDENTIALS_SERVER_ERROR;
import static momomomo.dungeonwalker.ui.html.domain.outbound.security.SecurityGatewayException.Type.CREDENTIALS_UNMAPPED_ERROR;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;

@Slf4j
@Component
@RequiredArgsConstructor
public class KeycloakGateway implements SecurityGateway {

    private static final String GRANT_TYPE_ATTR = "grant_type";
    private static final String CLIENT_ID_ATTR = "client_id";
    private static final String USERNAME_ATTR = "username";
    private static final String PASSWORD_ATTR = "password";
    private static final String GRANT_TYPE_VALUE = "password";

    private final KeycloakProperties properties;
    private final ObjectMapper jsonMapper;

    private final RestClient restClient = RestClient.create();

    public TokenResponse requestToken(@NonNull final String username, @NonNull final String password) {
        log.debug("Retrieving token for user: {}/{}", username, password);

        try {
            final var requestBody = new LinkedMultiValueMap<>();
            requestBody.put(GRANT_TYPE_ATTR, Collections.singletonList(GRANT_TYPE_VALUE));
            requestBody.put(CLIENT_ID_ATTR, Collections.singletonList(properties.getClientId()));
            requestBody.put(USERNAME_ATTR, Collections.singletonList(username));
            requestBody.put(PASSWORD_ATTR, Collections.singletonList(password));

            final ResponseEntity<TokenResponse> response = restClient
                    .post()
                    .uri("%s://%s/%s".formatted(properties.getProtocol(), properties.getHostPort(), properties.getPath()))
                    .contentType(APPLICATION_FORM_URLENCODED)
                    .body(requestBody)
                    .retrieve()
                    .toEntity(TokenResponse.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.debug("Token retrieved for user: {}/{} = {}", username, password, response.getBody());
                return response.getBody();
            }

            log.error("Credentials request didn't return error, but wasn't successful either for user: {}/{}", username, password);

            throw new SecurityGatewayException(
                    "Credentials request didn't return error, but wasn't successful either",
                    CREDENTIALS_UNMAPPED_ERROR);

        } catch (final HttpClientErrorException ex) {
            log.error("Error occurred while retrieving token for user: {}/{}. Error: {}",
                    username, password, ex.getMessage());

            if (ex.getStatusCode().is4xxClientError()) {
                // Remove prefixed message before JSON
                final var messageWithoutPrefix = ex.getMessage().replace("400 Bad Request:", "").trim();
                // Remove double quotes wrapping the json
                final var json = messageWithoutPrefix.substring(1, messageWithoutPrefix.length() - 1);

                final var description = jsonMapper
                        .readTree(json)
                        .path("error_description")
                        .asString("Authentication problem");

                throw new SecurityGatewayException(description, CREDENTIALS_CLIENT_ERROR);
            }

            if (ex.getStatusCode().is5xxServerError()) {
                throw new SecurityGatewayException("Authentication server error", CREDENTIALS_SERVER_ERROR);
            }

            throw new SecurityGatewayException(String.valueOf(ex.getStatusCode().value()), CREDENTIALS_UNMAPPED_ERROR);

        } catch (final Exception ex) {
            log.error("Unexpected error occurred while retrieving token for user: {}/{}. Error: {}",
                    username, password, ex.getMessage());

            throw new SecurityGatewayException(ex, CREDENTIALS_FATAL_ERROR);
        }

    }

}

# Dungeon Walker HTML User Interface

`dungeon-walker-ui-html` is the browser client for Dungeon Walker. It is a Spring Boot application that serves a
JSP-based game page, exchanges credentials for an access token, and connects the browser to the WebSocket game gateway.

## Responsibilities

- Serves the game interface and static JavaScript, CSS, and image assets.
- Obtains a Keycloak access token from supplied user credentials without exposing the Keycloak token endpoint to the
  browser.
- Supplies the page with the configured token-service and WebSocket endpoints.
- Translates player commands into WebSocket messages.
- Renders the dungeon state, cell changes, and connection activity in the browser.

The application listens on port `8082` by default.

## Architecture and request flow

```text
Browser                     UI application                  Keycloak / WebSocket gateway
   |                              |                                      |
   | GET /                        |                                      |
   |----------------------------->|                                      |
   |  JSP, static assets, config  |                                      |
   |<-----------------------------|                                      |
   |                              |                                      |
   | POST /token/credentials      | POST token password grant            |
   |----------------------------->|------------------------------------->|
   |  access token or error       |                                      |
   |<-----------------------------|<-------------------------------------|
   |                                                                     |
   | WebSocket authentication and game messages                          |
   |-------------------------------------------------------------------->|
```

1. `GET /` is handled by `IndexController`, which renders `index.jsp` and injects the configured message limit, token
   endpoint, and WebSocket endpoint.
2. The browser accepts a `login <username> <password>` command and posts the credentials to `POST /token/credentials`.
3. `TokenController` delegates to `SecurityService`, whose `KeycloakGateway` submits a form-encoded OAuth/OpenID Connect
   password-grant request to Keycloak.
4. On a successful response, the browser creates a WebSocket connection and authenticates with the returned access
   token.
5. Once authenticated, the browser sends heartbeats and movement commands, while rendering game-state messages from the
   gateway.

## Browser commands

| Command                                    | Effect                                       |
|--------------------------------------------|----------------------------------------------|
| `help` or `h`                              | Shows command help.                          |
| `login <username> <password>` or `li ...`  | Obtains a token and opens a game connection. |
| `logout` or `lo`                           | Closes the WebSocket connection.             |
| `N`, `S`, `E`, `W`, `NE`, `NW`, `SE`, `SW` | Sends a movement command.                    |

## WebSocket protocol used by the UI

After opening the configured WebSocket endpoint, the UI sends JSON messages of these forms:

```json
{
  "type": "authentication",
  "data": {
    "token": "<access-token>"
  }
}
```

```json
{
  "type": "heartbeat",
  "data": {
    "timestamp": "<current-time>"
  }
}
```

```json
{
  "type": "movement",
  "data": {
    "direction": "NE"
  }
}
```

It handles `authentication`, `heartbeat`, `server-errors`, `client-errors`, `server-message`, `dungeon-state`, and
`cell-state` responses. A successful authentication starts a 10-second heartbeat interval.

## Dungeon rendering

The client dynamically builds an HTML table when it receives the first `dungeon-state` message. It then applies full
dungeon or individual cell updates:

- IDs matching a wall convention (`W-` prefix with five characters) render as bricks.
- Other IDs render as walkers.
- Walker icons indicate the inferred direction of movement.
- The message panel prepends connection, authentication, error, heartbeat, and game messages, retaining the configured
  maximum number of entries.

## Configuration

Configuration is in `src/main/resources/application.yml`.

| Setting                   | Default                                     | Purpose                                     |
|---------------------------|---------------------------------------------|---------------------------------------------|
| `server.port`             | `8082`                                      | UI application port.                        |
| `keycloak.token.*`        | `http://localhost:8087/.../token`           | Keycloak client and token endpoint details. |
| `view.security.*`         | `http://localhost:8082/token/credentials`   | Browser-visible token endpoint.             |
| `view.web-socket.*`       | `ws://localhost:8085/ws-server/ws-endpoint` | Browser-visible game gateway endpoint.      |
| `view.messages.max-count` | `30`                                        | Maximum visible log messages.               |

`AUTH_SERVER_PROTOCOL` and `AUTH_SERVER_HOST` can override the Keycloak protocol and host/port.

## Current implementation caveats

- The browser checks a `status` field that the token endpoint does not return. A failed token request can consequently
  progress to WebSocket authentication with an undefined token.
- Debug logging currently includes credentials and issued tokens in the Java authentication path. Do not enable or
  retain these logs in production.
- Incremental cell updates for coordinate `x = 0` or `y = 0` are skipped because those values are tested as truthy
  rather than for presence.

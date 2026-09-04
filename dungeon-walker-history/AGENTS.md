# Dungeon Walker History

## Purpose

Dungeon Walker History is an event-driven history service for Dungeon Walker. It consumes dungeon-change events from the
engine, persists historical walker state in PostgreSQL, and supports reconstructing how a dungeon or individual walkers
changed over time.

## Layered architecture

The repository is a Maven reactor with four modules. Keep dependencies directed inward:

| Module              | Layer                     | Responsibility                                                                                                                                                                            |
|---------------------|---------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `history-domain`    | Domain                    | Business models, input/output data, factories, inbound ports, and outbound gateway contracts. It must not depend on Spring, persistence, messaging, or other application modules.         |
| `history-core`      | Application               | Implements domain use cases such as `HistoryLogPort` and coordinates domain factories with gateway interfaces. It depends only on `history-domain`.                                       |
| `history-transport` | Infrastructure / adapters | Consumes AMQP messages, maps contract payloads to domain inputs, and implements persistence gateways with Spring Data JPA. It depends on `history-domain` and `dungeon-walker-contracts`. |
| `history-startup`   | Composition / runtime     | Spring Boot entry point that component-scans all history packages and supplies operations, service discovery, observability, and integration tests. It is the only executable module.     |

Runtime flow: the engine publishes a `HistoryLog` to RabbitMQ; the Spring Cloud Stream `consumeHistoryLog` consumer maps
it to a domain `HistoryLogInput`; core processes the `HistoryLogPort`; transport persists it through `HistoryGateway`
and JPA.

## Technology stack

- Java 25, Maven, and a Spring Boot 4.1.0 multi-module build
- Spring Cloud Stream and Spring Functions with the RabbitMQ binder for asynchronous messaging
- Protobuf-based `dungeon-walker-contracts` messages at the service boundary
- Spring Data JPA and PostgreSQL persistence
- Spring Cloud Config and Netflix Eureka for external configuration and service discovery
- Spring Boot Actuator, Micrometer Prometheus registry, and OpenTelemetry Java agent for observability
- Lombok, Apache Commons Lang, JUnit 5, Cucumber, AssertJ, and Testcontainers for development and testing
- Jib for container image construction

## Repository layout

- `history-domain/src/main/java/.../domain`: domain API and model. Preserve framework independence here.
- `history-core/src/main/java/.../core`: application-service implementations of domain inbound ports.
- `history-transport/src/main/java/.../transport`: inbound message adapters, JPA repositories, gateway implementations,
  and mappers.
- `history-startup/src/main/java/.../startup`: the `DungeonWalkerHistory` Spring Boot entry point.
- `history-startup/src/test`: Cucumber feature tests, step definitions, JSON fixtures, and Testcontainers integration
  configuration.
- `README.md`: service purpose and messaging background.

## Implementation rules

- Add new use cases as domain ports plus core implementations; do not call transport classes from core or domain.
- Add external inputs in `history-transport` and map them into domain input objects before invoking a domain port.
- Add persistence and other external outputs as implementations of domain gateway interfaces in `history-transport`.
- Keep Protobuf contract types at the transport boundary. Do not expose them in domain or core APIs.
- Use domain factories to create and validate domain objects. Preserve explicit failures for unsupported message types
  and missing adapters.
- Place Spring stereotypes, JPA annotations, messaging bindings, and external-system details outside `history-domain`.
- Keep module dependencies consistent with the table above. `history-startup` may compose all modules; inner modules
  must not depend on outer ones.

## Configuration and operations

- `history-startup/src/main/resources/application.yml` defines `dungeon-walker-history`, selects `APP_PROFILE` (default
  `dev`), and imports configuration from `CONFIG_SERVER_URL` (default `http://localhost:8083/`).
- The `consumeHistoryLog` binding receives from `history-log-exchange` with the application name as its consumer group.
  Keep the Java bean name and configured function definition aligned.
- Do not hard-code environment endpoints or credentials. Use the existing externalized configuration and
  environment-variable pattern.
- The application exposes Actuator endpoints and Prometheus metrics; preserve readiness and liveness health probes when
  changing runtime wiring.

## Build and test

Run Maven from this repository root:

```bash
mvn test
```

Run only the startup integration suite:

```bash
mvn -pl history-startup test
```

The integration suite starts PostgreSQL and RabbitMQ with Testcontainers, so Docker must be available. Build the
executable container image with:

```bash
mvn -pl history-startup jib:dockerBuild
```

The reactor requires the `momomomo.dungeonwalker:dungeon-walker-contracts:1.0-SNAPSHOT` artifact. Build/install the
contracts module from the parent workspace first if Maven cannot resolve it.

## Code and test conventions

- Follow existing package names rooted at `momomomo.dungeonwalker.history`.
- Prefer constructor injection and `final` method parameters, consistent with the existing Spring components.
- Write end-to-end behavior in `history-startup/src/test/resources/features/DungeonWalkerHistory.feature`; keep message
  fixtures in the corresponding `data/inbound` and `data/outbound` directories.
- Test observable behavior, including persisted records and message-to-domain mapping, rather than implementation
  details.
- Update this file and `README.md` when changing the service boundary, module responsibilities, or local development
  workflow.

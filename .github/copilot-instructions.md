# Copilot instructions — dungeon-walker

Purpose: Provide concise, repository-specific guidance for Copilot sessions so future agents can act correctly and quickly.

---

## Quick build & run

- Full multi-module build (helper script):
  - ./mvn-clean-install.sh
    - Runs mvn clean install across modules in sequence (see script for order).
- Per-module Maven build & run (recommended when iterating):
  - cd <module-directory>
  - mvn clean install -U
  - To skip tests: mvn clean install -DskipTests
- Docker-based local environment (recommended end-to-end):
  - cd dungeon-walker-docker
  - sh build-jre-image-with-curl.sh
  - sh build-n-run-service-images.sh
  - Open: http://localhost:8082

## Tests

- Run all tests in a module:
  - cd <module-directory>
  - mvn test
- Run a single test class or method (from module dir):
  - mvn -Dtest=MyTestClass test
  - mvn -Dtest=MyTestClass#someMethod test
- Integration / feature tests: some modules include Cucumber features under src/test/resources/features — run via mvn test in the module.

## Lint / static analysis

- No repository-level linter/formatter command found. ARCHITECTURE.md recommends adding linters in CI; none are configured currently. Use IDE inspections or add Checkstyle/SpotBugs plugins at the module POM level if needed.

---

## High-level architecture (short)

- Multi-module Java microservices (Spring Boot) arranged as independent Maven modules.
- Key roles:
  - dungeon-walker-engine — core game logic (Akka/Pekko actor model).
  - dungeon-walker-ws-server — WebSocket gateway (one Session actor per socket), converts client JSON ↔ internal Protobuf/Kafka messages.
  - dungeon-walker-config-server, discovery-server, gateway-server — service infrastructure (Spring Cloud patterns).
  - dungeon-walker-contracts — Protobuf schemas used across services.
  - dungeon-walker-ui-html — simple HTML/vanilla JS client (websocket-based).
- Services communicate via Kafka topics (commands/events). Configuration uses HOCON (application.conf) and YAML in config-server.
- Docker scripts under dungeon-walker-docker build images and bring up a local environment (recommended for end-to-end testing).

Refer to: dungeon-walker-ws-server/ARCHITECTURE.md and top-level README.md for diagrams and longer explanations.

---

## Key repository conventions

- Module scope: each subfolder is a standalone Maven module. Work inside the module when running mvn commands.
- Contracts-first: Protobufs in dungeon-walker-contracts define Kafka message formats. Keep changes backward-compatible.
- WebSocket gateway conventions (ws-server): one actor per active WebSocket session; translate JSON envelopes on the wire to Protobuf for internal Kafka messages.
- Configuration: environment-specific YAML/HOCON under dungeon-walker-config-server/resources/config and per-module application.conf/yaml. Prefer externalized config.
- Documentation & code quality (local policy): follow the guidance in dungeon-walker-ws-server/AGENTS.md — SOLID, Javadoc for public types, minimal utility classes, and test-first when possible. AGENTS.md is authoritative for code style and design decisions in the ws-server area.

Files of interest to reference quickly:
- README.md (root)
- dungeon-walker-ws-server/ARCHITECTURE.md
- dungeon-walker-ws-server/AGENTS.md
- dungeon-walker-docker/ (docker build & run helper scripts)
- mvn-clean-install.sh

---

If changing code: prefer small surgical edits, run the module's mvn test, and validate changes in the Docker environment when behavior spans services.

---

Created: .github/copilot-instructions.md (added by Copilot CLI)

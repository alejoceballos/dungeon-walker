# ARCHITECTURE — dungeon-walker-ws-server

This document explains the architecture of the "dungeon-walker-ws-server" module so it can be reproduced, audited, and regenerated (partially or fully) using AI-driven workflows in the future. It describes the module responsibilities, component interactions, technologies, SOLID application, design patterns, testing, deployment, and guidelines for using AI to recreate or evolve the module.

## Overview

The dungeon-walker-ws-server is the WebSocket-facing gateway for the Dungeon Walker engine. Its responsibilities:

- Accept and manage WebSocket connections from players and admin UIs.
- Translate socket messages into domain commands/events and validate them.
- Route commands/events to the internal processing pipeline (actor system + Kafka). 
- Subscribe to domain events and push relevant updates to connected clients.
- Maintain ephemeral session state and session lifecycle (connect/disconnect, heartbeats).

The module is implemented in Java using an asynchronous, actor-based concurrency model (Pekko), integrates with Kafka for durable event distribution, and exposes a high-throughput WebSocket API (Pekko HTTP / Pekko Streams or equivalent).

## High-level architecture

Components:

- WebSocket Layer
  - Accepts socket connections via Pekko HTTP.
  - Upgrades HTTP requests to WebSocket and maps each connection to a Session actor.
  - Handles authentication (JWT/session token) and basic rate-limiting.

- Session Actors (Pekko)
  - One actor per active WebSocket session.
  - Manages connection lifecycle, message buffering, outbound backpressure.
  - Maps incoming socket messages to domain Commands and forwards them to the Command Router.

- Command Router / Gateway
  - Validates and enriches commands, applies policies (authorization, rate limits), and publishes commands to Kafka or forwards them directly to local processing actors.
  - Produces structured events/commands to Kafka topics for the main engine (e.g., commands.topic, presence.topic).

- Event Consumer / Subscription Manager
  - Subscribes to Kafka topics for domain events produced by game engine workers and NPC processors.
  - Matches events to sessions/players and forwards events to the appropriate Session Actor for delivery to the client.

- Serialization & Schema
  - Prefer Protobuf (or Avro) schemas for Kafka messages for backward compatibility and clear contracts.
  - WebSocket payloads use a compact JSON envelope for ease of debugging and to support browsers; convert to/from Protobuf internally.

- Persistence
  - Minimal durable session metadata (e.g., last-seen, reconnect tokens) stored in a fast store (Redis or PostgreSQL), but main authoritative state is event-driven in the engine services.

- Observability
  - Structured logs (SLF4J + Logback), metrics (Prometheus + Micrometer), and distributed traces (OpenTelemetry).

- Configuration
  - Externalized via typesafe config (HOCON) or environment variables. Sensitive values read from a secrets store at runtime.

## Technologies and rationale

- Java (primary language)
  - Strong ecosystem, stable tooling, and good interop with Pekko.

- Pekko (actor model)
  - Used for lightweight, non-blocking concurrent Session actors and internal routing.
  - Pekko Streams for backpressure-aware message flow between the WebSocket surface and internal processing.

- Kafka
  - Durable, horizontally scalable event bus to decouple WebSocket gateway from game processing workers.
  - Use topics with consumer groups (e.g., per region or feature) to scale consumers.

- Serialization
  - Protobuf or Avro for Kafka messages (schema registry recommended).
  - WebSocket messages encoded as compact JSON envelopes to keep browser clients simple.

- Persistence
  - Redis for ephemeral session tokens and presence; Postgres for long-lived metadata and migrations (Flyway).

- Build & tooling
  - Gradle (or Maven) for builds. JUnit + Mockito for tests. Testcontainers for integration tests with Kafka and Redis.

- Observability
  - Micrometer -> Prometheus, OpenTelemetry for traces.

- CI/CD
  - GitHub Actions pipeline builds, runs tests, and performs static analysis. Containerize the module (Docker) and publish images to a registry.

## Package / Module structure (recommended)

- src/main/java/
  - com.dungeonwalker.ws
    - api
      - websocket: HTTP/WebSocket endpoints, upgrade logic
      - dto: JSON envelope DTOs
    - session
      - SessionActor.java
      - SessionManager.java
      - SessionRepository.java (interface)
    - routing
      - CommandRouter.java
      - EventDispatcher.java
    - kafka
      - KafkaProducerService.java
      - KafkaConsumerWorker.java
      - serializers (Protobuf converters)
    - domain
      - commands
      - events
      - models
    - persistence
      - RedisSessionStore.java
      - PostgresMetadataStore.java
    - di
      - Module.java (wiring for DI framework)
    - util
      - BackpressureUtils.java
      - SerializationUtils.java

This layout maps responsibilities to packages and keeps a clear boundary between I/O (api), concurrency (session), integration (kafka), and domain logic.

## SOLID principles applied

- Single Responsibility Principle (SRP)
  - Each class focuses on a single concern: SessionActor handles the socket session lifecycle; CommandRouter handles routing and enrichment; KafkaProducerService only deals with producing to Kafka.

- Open/Closed Principle (OCP)
  - Define handler interfaces (e.g., CommandHandler, EventHandler) and register implementations for new behaviors (new command types) without changing the router internals.

- Liskov Substitution Principle (LSP)
  - Favor small interfaces with consistent contracts; implementations of CommandHandler must honor the pre- and post-conditions (e.g., validation results, exceptions) so substitutability is safe.

- Interface Segregation Principle (ISP)
  - Provide focused interfaces: PresenceStore vs. SessionStore rather than a monolithic Store interface. WebSocket serializer/codec interfaces separate transport concerns from domain serialization.

- Dependency Inversion Principle (DIP)
  - High-level modules (e.g., CommandRouter) depend on abstractions (CommandProducer) rather than concrete KafkaProducerService. Use constructor injection for easier testing and AI-driven generation of mocks.

## Design patterns used

- Actor Model (Pekko)
  - Primary concurrency pattern: one actor per session; supervisors manage actor lifecycle and failure recovery.

- Publish/Subscribe
  - WebSocket gateway publishes commands to Kafka; processing services publish events that the gateway subscribes to.

- Strategy
  - Command validation/ratelimiting/authorization are pluggable strategies.

- Command pattern
  - Map client actions to Command objects that can be validated, logged, serialized, and replayed.

- Factory / Abstract Factory
  - For creating typed Command and Event instances from raw DTOs.

- Adapter
  - Wrap external systems (Kafka, Redis) behind small adapter interfaces to decouple them from internal logic.

- Repository
  - Abstract persistence concerns for session metadata, enabling test doubles and future storage changes.

- Decorator / Middleware
  - Message pipelines (validation -> enrichment -> authorization -> production) implemented as chainable decorators or middleware steps.

## Concurrency, backpressure and flow control

- Use Pekko Streams to connect WebSocket source/sink to Session actors to enforce backpressure.
- SessionActor buffers outbound messages with a bounded mailbox and applies drop/slow-client policies.
- When producing to Kafka, use async produces with callback handling and appropriate retries and circuit-breaker policies to avoid blocking actors.

## Security considerations

- Authenticate on WebSocket upgrade (JWT or session token). Reject unauthorized upgrades.
- Authorize each command according to player permissions.
- Use TLS for WebSocket (wss://) in production.
- Sanitize and validate inputs; apply size limits to messages; throttle abusive clients.

## Testing strategy

- Unit tests for CommandRouter, serialization, and business rules.
- Actor unit tests using Pekko TestKit / Pekko typed testkit.
- Integration tests with Testcontainers: stand up Kafka, Redis, Postgres.
- End-to-end tests (optional): run the ws-server against a local engine worker in CI for smoke tests.

## Deployment and scaling

- Containerize the module and deploy behind a load balancer. For sticky WebSocket sessions, use a session-affinity (or use a shared session store and route any instance) or a stateless token that allows reconnects.
- Horizontally scale by running multiple instances; Kafka decouples producers/consumers so scaling is straightforward.
- Use autoscaling based on metrics: connection count, throughput, consumer lag.

## Observability and debugging

- Emit structured events for key lifecycle events: connect, disconnect, command received, command published, event delivered.
- Correlate traces across the gateway and worker services using a shared trace id in message envelopes.
- Expose health and readiness endpoints for orchestrators.

## Reproducibility using AI models

The goal is to be able to replicate (generate) this module using AI-assisted coding. Below is a recommended workflow and constraints to make that repeatable and auditable.

1. Source artifacts and constraints
   - Provide Protobuf/Avro schemas for command & event contracts.
   - Provide a HOCON configuration schema (example file).
   - Provide a minimal interface catalog (core Java interfaces with Javadoc) for DI and adapters.
   - Provide tests that clearly assert behavior (unit + integration). These tests serve as the specification for AI-generated code (test-driven generation).

2. Prompt engineering guidance
   - Use small, focused prompts: "Generate SessionActor.java that maintains a bounded buffer and sends JSON envelopes to a WebSocket sink using Pekko Streams. Follow this interface: ...".
   - Provide schema files and package structure in the prompt or as accompanying files.
   - Use multi-step generation: generate interfaces and tests first, then implementations.

3. Iterative verification
   - Validate generated code against unit tests locally (CI runs). Failing tests narrow the next generation iteration.
   - Use semantic search to find existing similar patterns in the repo and reuse those patterns.

4. Safety and determinism
   - Pin versions of dependencies in the build file to ensure deterministic builds.
   - Use linters and static analyzers in CI to enforce consistent style and to catch risky constructs.

5. Human-in-the-loop review
   - Always open a PR for generated changes with human reviewers and automated checks before merging.

## Example guidelines for AI-generated class skeletons

- Always prefer explicit interfaces with small, well-documented method contracts.
- Keep side-effects isolated behind adapter interfaces.
- Provide comprehensive unit tests that exercise both success and failure cases.

Example minimal interface (for AI to implement):

interface CommandProducer {
  CompletionStage<RecordMetadata> produce(CommandEnvelope command);
}

interface SessionStore {
  CompletionStage<Void> persist(SessionMetadata s);
  CompletionStage<Optional<SessionMetadata>> load(String sessionId);
}

These simple interfaces give AI a bounded surface to implement and test.

## Operational checklist

- Ensure Kafka topic schemas and retention settings are defined and versioned.
- Ensure schema registry is available for Protobuf/Avro schemas.
- Ensure secrets and TLS certs are provisioned for production.
- Monitor consumer lag and set alerts for high-latency event delivery.

## Migration and extensibility

- When adding new command types, add a new Protobuf message and handler implementation; keep old fields optional to preserve backwards compatibility.
- Avoid changing existing topic semantics; create new topics or versions if necessary.

## Appendix: Quick-start to spin up locally (developer workflow)

1. Start Kafka, Redis, Postgres locally (Testcontainers or docker-compose).
2. Configure env variables (KAFKA_BOOTSTRAP, REDIS_URL, DATABASE_URL).
3. Run ./gradlew :dungeon-walker-ws-server:run
4. Connect a WebSocket client to ws://localhost:8080/ws and exchange JSON envelopes.

---

If you want, I can also:
- add example Protobuf schemas and minimal Java interfaces to the module,
- add a sample SessionActor skeleton implementation and unit tests so AI can use concrete examples,
- or open a PR with these changes applied.

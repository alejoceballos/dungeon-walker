# ARCHITECTURE — dungeon-walker-ws-server

This document strictly describes the architecture that can be observed in the repository code for the dungeon-walker-ws-server Java module. It contains only facts that appear in the codebase (class names, configuration, modules and technologies referenced). It is intended as an executable specification that future AI agents can use to reproduce the module structure and wiring.

Summary
- Languages & build: Java (pom.xml modules), Maven build (pom files), Docker image build via Jib (jib-maven-plugin) and repository scripts.
- Frameworks and libraries referenced in code: Spring Boot, Spring WebSocket, Spring Kafka, Spring Cloud (config, Eureka client), Apache Pekko (actor typed, cluster sharding), Micrometer, OpenTelemetry (agent dependency), Testcontainers (test scope), Protobuf-generated types referenced from contract packages.
- Integration points visible in code: WebSocket transport (Spring WebSocket), Kafka producers/consumers (Spring Kafka), Pekko actor system / cluster sharding (Pekko Config + GuardianActor + Entity actors), JSON mapping (Jackson ObjectMapper bean), JWT-based handshake/authorization (NimbusJwtDecoder usage).

Project module boundaries
The parent ws-server pom declares four modules that form the boundary of this project:

- wsserver-domain
- wsserver-core
- wsserver-transport
- wsserver-startup

These modules are present in the parent pom and are implemented in their respective directories.

Clean-architecture mapping (from code)
From the repository code the modules map to a layered, clean-architecture style separation:

- wsserver-domain — Domain layer (enterprise/business rules)
  - Contains domain-level interfaces, data objects and handler selectors that do not depend on transport or framework classes.
  - Evidence in code:
    - Interfaces: momomomo.dungeonwalker.wsserver.domain.inbound.UserInbound, EngineInbound
    - Handler selector interface: momomomo.dungeonwalker.wsserver.domain.handler.MessageHandlerSelector
    - Domain data: momomomo.dungeonwalker.wsserver.domain.data.* (example: Direction enum)
  - Role: define contracts used by core/transport; no framework wiring in domain sources.

- wsserver-core — Application / use-case layer (Pekko actor orchestration and message managers)
  - Contains Pekko actor configuration and actors that implement application behaviour and routing.
  - Evidence in code:
    - Pekko configuration: momomomo.dungeonwalker.wsserver.core.config.PekkoConfig (creates ActorSystem and ClusterSharding)
    - Guardian actor: momomomo.dungeonwalker.wsserver.core.actor.guardian.GuardianActor
    - Cluster sharding manager and actor wiring: momomomo.dungeonwalker.wsserver.core.actor.ClusterShardingManager
    - Inbound managers (map transport messages into actor commands): EngineMessageManager and UserMessageManager under wsserver-core.inbound.*
    - Actor command records and command types in wsserver-core.actor.* packages (e.g., ConnectionCloseCommand, UserHeartbeatCommand)
  - Role: implement use-cases and map domain-level contracts to actor messages and cluster-sharding entities.

- wsserver-transport — Interface adapters layer (WebSocket + Kafka integration and serialization)
  - Implements the external-facing transport details and adapters that convert transport messages into domain inputs and vice-versa.
  - Evidence in code:
    - WebSocket handler and transport config: momomomo.dungeonwalker.wsserver.transport.inbound.WsHandler and WebSocketConfig
    - WebSocket session adapter: momomomo.dungeonwalker.wsserver.transport.connection.WebSocketSessionAdapter (implements domain outbound UserConnection)
    - JWT handshake/authorization: JwtHandshakeInterceptor and JwtAuthorizer (authorization implementation) using NimbusJwtDecoder
    - Kafka integration and configuration: momomomo.dungeonwalker.wsserver.transport.config.KafkaConfig and KafkaConsumer class under transport.inbound
    - Application transport properties: wsserver-transport/src/main/resources/application-transport.yml (defines websocket.endpoint, Kafka serializer/deserializer classes)
    - JSON mapper bean: WsTransportConfig defines ObjectMapper bean
    - Transport uses contract proto types in Kafka consumers/producers (contract.client.ClientRequestProto and contract.engine.EngineMessageProto referenced)
  - Role: adapt WebSocket messages to domain Input objects, produce/consume Kafka messages and call domain inbound interfaces (UserInbound, EngineInbound).

- wsserver-startup — Framework/bootstrap layer (Spring Boot wiring and runtime dependencies)
  - Contains dependencies and configuration for runtime concerns, monitoring and service discovery.
  - Evidence in code and pom:
    - pom declares dependencies: spring-boot-starter-actuator, spring-cloud-starter-config, spring-cloud-starter-netflix-eureka-client, micrometer-registry-prometheus, opentelemetry-javaagent (runtime scope)
    - wsserver-startup module depends on wsserver-domain, wsserver-core and wsserver-transport in its pom, indicating it composes the other modules into a runnable application.
  - Role: provide Spring Boot application bootstrap, actuator/monitoring and cloud config/discovery wiring.

Inter-module relations (how modules interact in code)
- Transport -> Domain
  - WsHandler uses UserInbound (domain interface) to establish/close/handle user messages. (WsHandler.createClientInbound returns WebSocketSessionAdapter which implements domain.outbound.UserConnection.)
  - KafkaConsumer in transport delegates consumed EngineMessage proto instances to a domain EngineInbound bean.

- Core -> Domain
  - Core implements EngineInbound and UserInbound (EngineMessageManager and UserMessageManager are annotated @Component and implement domain inbound interfaces) and translate messages into Pekko actor commands via ClusterShardingManager.
  - ClusterShardingManager in core initializes Pekko entities (ConnectionActor, ClientActor) and exposes methods to obtain EntityRef and tell/ask actors.

- Core <-> Transport (indirect coupling via domain interfaces)
  - Transport calls domain interfaces (UserInbound, EngineInbound) — the implementations are provided by wsserver-core components.
  - Transport provides adapters (WebSocketSessionAdapter implements UserConnection) that are consumed by core inbound managers.

- Startup -> {domain, core, transport}
  - The startup module depends on the other modules and provides the Spring Boot application/bootstrap context that brings transport beans, core beans and domain contracts together at runtime.

Transport boundaries and technologies (explicit in code)
- WebSocket boundary (HTTP -> WebSocket): implemented with Spring WebSocket
  - Class: momomomo.dungeonwalker.wsserver.transport.config.WebSocketConfig registers the WsHandler under the configured endpoint.
  - Handler: momomomo.dungeonwalker.wsserver.transport.inbound.WsHandler extends TextWebSocketHandler and converts TextMessage payloads to domain Input via Jackson ObjectMapper.
  - Session adapter: WebSocketSessionAdapter adapts Spring WebSocketSession to the domain.outbound.UserConnection interface used by core.
  - JWT handshake: JwtHandshakeInterceptor inspects Authorization header during handshake; JwtAuthorizer decodes and validates tokens.

- Kafka boundary (service-to-service event bus): implemented with Spring Kafka
  - Transport Kafka configuration: momomomo.dungeonwalker.wsserver.transport.config.KafkaConfig creates ConsumerFactory, ProducerFactory and KafkaTemplate beans using properties from application-transport.yml.
  - Kafka consumer: KafkaConsumer (transport.inbound) listens to a configured topic and delegates EngineMessage proto instances to the domain EngineInbound handler.
  - Code references to contract proto classes show that message contracts use Protobuf-generated types (contract.client.ClientRequestProto.ClientRequest, contract.engine.EngineMessageProto.EngineMessage).
  - application-transport.yml references custom serializer/deserializer classes for Kafka values (package names present in config file), showing explicit serializer wiring in code configuration.

Actor system and concurrency
- Pekko actor system is configured and created in wsserver-core via PekkoConfig (uses Typesafe Config / HOCON — ConfigFactory.load("application.conf")).
- ClusterSharding is created and used to initialize ConnectionActor and ClientActor entity types (ClusterShardingManager.init calls clusterSharding.init with Entities for ConnectionActor and ClientActor).
- GuardianActor is present in wsserver-core and used as the Pekko root behavior when creating the ActorSystem.

Patterns and object-oriented principles visible in code
- Actor model (Pekko) — core actors (GuardianActor, ConnectionActor, ClientActor) implement application concurrency and entity lifecycle.
- Adapter pattern — WebSocketSessionAdapter adapts framework WebSocketSession to the domain UserConnection interface.
- Dependency Injection / Inversion of Control — Spring @Component, @Configuration and constructor injection are used across transport and core (beans injected via constructors and @RequiredArgsConstructor).
- Separation of concerns / layered design — domain defines interfaces and types; transport implements framework adapters; core implements application behaviour and actor orchestration.
- Selector/Strategy style — *Selector classes exist to choose handlers/mappers at runtime (examples: UserInputMapperSelector, EngineMessageMapperSelector, MessageHandlerSelector interface) indicating pluggable mapping strategies.
- Explicit typed message contracts — code references Protobuf-generated classes from the contracts module (client and engine proto types), showing a declared contract layer.

Concrete class examples (points of reference)
- WebSocket transport and JSON mapping: wsserver-transport/src/main/java/.../WsHandler.java, WebSocketSessionAdapter.java, WsTransportConfig.java, WebSocketConfig.java
- Kafka transport: wsserver-transport/src/main/java/.../config/KafkaConfig.java and wsserver-transport/src/main/java/.../inbound/KafkaConsumer.java
- Pekko core and actors: wsserver-core/src/main/java/.../config/PekkoConfig.java, wsserver-core/src/main/java/.../actor/guardian/GuardianActor.java, wsserver-core/src/main/java/.../actor/ClusterShardingManager.java
- Core inbound managers: wsserver-core/src/main/java/.../inbound/engine/EngineMessageManager.java, wsserver-core/src/main/java/.../inbound/user/UserMessageManager.java
- Domain interfaces and data: wsserver-domain/src/main/java/.../domain/inbound/UserInbound.java, EngineInbound.java, domain.handler.MessageHandlerSelector.java, domain.data.*
- Startup / runtime wiring: wsserver-startup/pom.xml (declares Spring Boot actuator, Spring Cloud dependencies and module dependencies)

What this file intentionally does NOT contain
- Any recommendations, guidelines or technologies that are not present in repository code.
- Implementation details not observable in the code (no hypothetical persistence stores, no speculative CI setup beyond what appears in pom and scripts).

Notes about sources and completeness
- All statements in this file are derived from files present in the repository (pom.xml files, Java sources, YAML resources and shell scripts). Code search used to assemble these facts returned a subset of repository files; results may be incomplete. For the full set of files referenced here see the project in the repository UI: https://github.com/alejoceballos/dungeon-walker/tree/main/dungeon-walker-ws-server

End of file.

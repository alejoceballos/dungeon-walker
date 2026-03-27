# Dungeon Walker Gateway Server

A unique entrypoint for incoming messages from the outer world.

> See [Spring Cloud Gateway](https://spring.io/projects/spring-cloud-gateway) reference.

### Create a new Spring Project

### In `pom-xml`, the main dependencies:

- Gateway
- Spring Actuator

### In the main class, add the annotation:

- `?`

### In the `application.yml`, the main details are:

Add the following properties to enable health checks:

- management.endpoints.web.exposure.include: "*"
- management.health.readiness-state.enabled: true
- management.health.liveness-state.enabled: true
- management.endpoint.health.probes.enabled: true
- management.endpoint.gtaeway.access: unrestricted
- spring.cloud.gateway.server.webflux.discovery.locator.enabled: true

### Add config files to folder `resources/config`:

The foolder is `resources/config` because in the `spring.cloud.config.server.native.search-locations`property, its
value is `classpath:/config`. There, create "some-service-name-<profile>.yml" for each service and their profile.

Example:

- dungeon-walker-engine-dev.yml
- dungeon-walker-engine-stg.yml
- dungeon-walker-ws-server-dev.yml
- dungeon-walker-ws-server-stg.yml

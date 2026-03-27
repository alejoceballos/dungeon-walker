# Dungeon Walker Configuration Server

A centralized configuration server for all Dungeon Walker services.

> See [Spring Cloud Config](https://docs.spring.io/spring-cloud-config/) reference.

### Create a new Spring Project

### In `pom-xml`, the main dependencies:

- Spring Cloud Config Server
- Spring Actuator

### In the main class, add the annotation:

- `EnableConfigServer`

### In the `application.yml`, the main details are:

- spring.profiles.active: native
- spring.cloud.config.server.native.search-locations: "classpath:/config"

Add the following properties to enable health checks:

- management.endpoints.web.exposure.include: "*"
- management.health.readiness-state.enabled: true
- management.health.liveness-state.enabled: true
- management.health.endpoint.health.probes.enabled: true

### Add config files to folder `resources/config`:

The foolder is `resources/config` because in the `spring.cloud.config.server.native.search-locations`property, its
value is `classpath:/config`. There, create "some-service-name-<profile>.yml" for each service and their profile.

Example:

- dungeon-walker-engine-dev.yml
- dungeon-walker-engine-stg.yml
- dungeon-walker-ws-server-dev.yml
- dungeon-walker-ws-server-stg.yml

### Enable each service to access the Config Server:

#### Add dependencies:

- Spring Cloud Config Client

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-config</artifactId>
</dependency>
```

### Set the configuration for each service

Each application.yml in the target service must have:

- `spring.application.name` property that must match to the file prefix in the config server.
- `spring.profiles.active` property set to the profile of the config file that will be loaded from the config server.
- `spring.config.import` with the URL of the config server mainly in the format: `configserver:url:port`.

### On-line properties check

Check the configuration for each server with a yml file located in the config server.

For example, check the following URL:

```
http://localhost:8083/dungeon-walker-engine/dev
```
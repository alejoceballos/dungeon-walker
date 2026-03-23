# Dungeon Walker Discovery Server

A Spring Cloud Discovery Server that will be used to register all the services in the Dungeon Walker ecosystem.

### Create a new Spring Project

### In `pom-xml`, the main dependencies:

- Spring Cloud Netflix Eureka Server
- Spring Actuator

### In the main class, add the annotation:

- `EnableEurekaServer`

### In the `application.yml`, the main details are:

Add the following properties to enable health checks:

- management.endpoints.web.exposure.include: "*"
- management.health.readiness-state.enabled: true
- management.health.liveness-state.enabled: true
- management.health.endpoint.health.probes.enabled: true

### Enable each service to register in the Eureka Server:

#### Add dependencies:

- Netflix Eureka Client

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
 ```

#### Configure service properties:

- eureka.client.serviceUrl.defaultZone: http://<discovery-server-url>:<discovery-server-port>/eureka/
- eureka.client.registerWithEureka: true
- eureka.client.fetchRegistry: true
- eureka.instance.preferIpAddress: true (for localhost) or false (for docker)

Enable the shutdown endpoint allowing to gracefully shut down the service:

- management.endpoint.shutdown.access: unrestricted

In case of any service using Spring Security, remember to configure a bypass for the actuator endpoints:

```java
@Bean
public SecurityFilterChain securityWebFilterChain(final HttpSecurity http) {
    return http
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/actuator/**")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
            .build();
}
```

### Eureka dashboard

To access the Eureka dashboard, go to:

```
http://localhost:8084/
```
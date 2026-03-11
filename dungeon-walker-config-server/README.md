# Dungeon Walker Configuration Server

### Create a new Spring Project

### In `pom-xml`, the main dependencies:
- Spring Cloud Server
- Spring Actuator

### In the main class, add the annotation:
- `EnableConfigServer`

### In teh `application.yml`, the main details are:
- spring.profiles.active: native
- spring.cloud.config.server.native.search-locations: "classpath:/config"

### Add config files to folder `resources/config`:
The foolder is `resources/config` because in the `spring.cloud.config.server.native.search-locations`property, its 
value is `classpath:/config`. There, create "some-service-name-<profile>.yml" for each service and their profile.

Example:
- dungeon-walker-engine-dev.yml
- dungeon-walker-engine-stg.yml
- dungeon-walker-ws-server-dev.yml
- dungeon-walker-ws-server-stg.yml

### Set the configuration for each service
Each application.yml in the target service must have:
- `spring.application.name` property that must match to the file prefix in the config server.
- `spring.profiles.active` property set to the profile of the config file that will be loaded from the config server.
- `spring.config.import` with the URL of the config server mainly in the format: `configserver:url:port`.
# Project Blueprint

## Goal

As a software engineer I want to create a project blueprint So I can use it to develop new application services

## Technologies

### Programming language

- Java, last stable version
- Do not install it, only check and inform if this requirement does not check

### Building Tool

- Maven, last stable version
- Do not install it, only check and inform if this requirement does not check

### Frameworks

- Spring Boot, last stable version

## Architecture

The resulting project must have (at least) 4 modules, called layers:

- `core`: where the main business rules reside. Depends only on the domain layer
- `transport`: the inbound and outbound communication with external systems. Depend only on the domain layer
- `domain`: holds structures and rules that belong to both, core and transport layers. Has no dependencies on other
  layers
- `startup`: the main layer responsible bootstrap the system and work as the inversion dependency context holder.
  Depends on all other layers.

## Pre-requisites

1. Have Java installed, version 25
2. Have Maven installed
3. Ask for the values of the following placeholders to be replaced:
    - `${service-name}`
4. When the placeholder `${camel-cased-service-name}` is encountered, replace it with the value of `${service-name}` in
   camel case format. For example, if `${service-name}` is `my-service`, then `${camel-cased-service-name}` should be
   `MyService`.

## Considerations

1. Perform one step at a time, and check if the step was successful before moving to the next one
2. Do not try to overwrite any existing directory or file, if a file or directory already exists, ask what to do before
   proceeding
2. Do not guess. If you are not sure about a step, ask for clarification before proceeding

### Steps:

1. Create the multi-module parent project:
    1. Create a directory named `dungeon-walker-${service-name}`
    2. Create a maven's `pom.xml` file
    3. Add Spring Boot `4.1.0` as parent dependency in the `pom.xml` file
    4. Add the following information to the `pom.xml` file:
        1. group-id: `momomomo.dungeonwalker`
        2. artifact-id: `dungeon-walker-${service-name}`
        3. name: `Dungeon Walker ${camel-cased-service-name}`
        4. version: `0.0.1-SNAPSHOT`
        5. packaging: `pom`
    5. Add the following properties to the `pom.xml` file:
        1. java.version: `25`
        2. maven.compiler.source: `25`
        3. maven.compiler.target: `25`
        4. project.build.sourceEncoding: `UTF-8`
        5. spring-cloud.version: `2025.1.1`
        6. jib.version: `3.5.1`
    6. Add the following modules to the `pom.xml` file:
        1. `${service-name}-core`
        2. `${service-name}-transport`
        3. `${service-name}-domain`
        4. `${service-name}-startup`
    7. Add the following dependencies to the `pom.xml` file:
        1. `spring-boot-starter`
        2. `spring-boot-starter-test`
    8. Add the following dependency management to the `pom.xml` file:
        1. `spring-cloud-dependencies` with version `${spring-cloud.version}`
    9. Add the following plugins to the `pom.xml` file:
        1. `maven-compiler-plugin` with `annotationProcessorPaths`:
            1. include `lombok`, but do not set any version
        2. `jib-maven-plugin` with:
            1. version: `${jib.version}`
            2. configuration:
                1. from.image: `alejoceballos/eclipse-temurin-25-jre-with-curl:v1`
                2. to.image: `alejoceballos/dungeon-walker-${service-name}:v1`
            2. container:
                1. mainClass: `momomomo.dungeonwalker.${service-name}.startup.DungeonWalker${camel-cased-service-name}`
                2. ports: `8086`
                3. jvmFlags: `--add-opens=java.base/jdk.internal.misc=ALL-UNNAMED`
2. Create the `domain` module:
    1. Create a directory named `${service-name}-domain`
    2. Create a maven's `pom.xml` file
    3. Add the following information to the `pom.xml` file:
        1. parent:
            1. group-id: `momomomo.dungeonwalker`
            2. artifact-id: `${service-name}-domain`
            3. version (use the same as the parent project)
        2. name: `Dungeon Walker ${camel-cased-service-name} - Domain`
        3. packaging: `jar`
    4. Add the following properties to the `pom.xml` file:
        1. jib.skip: `true`
    5. Add the following dependencies to the `pom.xml` file:
        1. `lombok`
    6. Create the following directory structure:
        1. `src/main/java/momomomo/dungeonwalker/${service-name}/domain`
            1. Create an empty file named `.gitkeep` inside this directory
        2. `src/main/resources`
            1. Create an empty file named `application-domain.yml` inside this directory
        3. `src/test/java/momomomo/dungeonwalker/${service-name}/domain`
            1. Create an empty file named `.gitkeep` inside this directory
3. Create the `core` module:
    1. Create a directory named `${service-name}-core`
    2. Create a maven's `pom.xml` file
    3. Add the following information to the `pom.xml` file:
        1. parent:
            1. group-id: `momomomo.dungeonwalker`
            2. artifact-id: `${service-name}-core`
            3. version (use the same as the parent project)
        2. name: `Dungeon Walker ${camel-cased-service-name} - Core`
        3. packaging: `jar`
    4. Add the following properties to the `pom.xml` file:
        1. jib.skip: `true`
    5. Add the following dependencies to the `pom.xml` file:
        1. `${service-name}-domain`
        2. `lombok`
    6. Create the following directory structure:
        1. `src/main/java/momomomo/dungeonwalker/${service-name}/core`
            1. Create an empty file named `.gitkeep` inside this directory
        2. `src/main/resources`
            1. Create an empty file named `application-core.yml` inside this directory
        3. `src/test/java/momomomo/dungeonwalker/${service-name}/core`
            1. Create an empty file named `.gitkeep` inside this directory
4. Create the `transport` module:
    1. Create a directory named `${service-name}-transport`
    2. Create a maven's `pom.xml` file
    3. Add the following information to the `pom.xml` file:
        1. parent:
            1. group-id: `momomomo.dungeonwalker`
            2. artifact-id: `${service-name}-transport`
            3. version (use the same as the parent project)
        2. name: `Dungeon Walker ${camel-cased-service-name} - Transport`
        3. packaging: `jar`
    4. Add the following properties to the `pom.xml` file:
        1. jib.skip: `true`
    5. Add the following dependencies to the `pom.xml` file:
        1. `${service-name}-domain`
        2. `lombok`
    6. Create the following directory structure:
        1. `src/main/java/momomomo/dungeonwalker/${service-name}/transport`
            1. Create an empty file named `.gitkeep` inside this directory
        2. `src/main/resources`
            1. Create an empty file named `application-transport.yml` inside this directory
        3. `src/test/java/momomomo/dungeonwalker/${service-name}/transport`
            1. Create an empty file named `.gitkeep` inside this directory
5. Create the `startup` module:
    1. Create a directory named `${service-name}-startup`
    2. Create a maven's `pom.xml` file
    3. Add the following information to the `pom.xml` file:
        1. parent:
            1. group-id: `momomomo.dungeonwalker`
            2. artifact-id: `${service-name}-startup`
            3. version (use the same as the parent project)
        2. name: `Dungeon Walker ${camel-cased-service-name} - Startup`
        3. packaging: `jar`
    4. Do not add `jib.skip` properties to the `pom.xml` file
    5. Add the following dependencies to the `pom.xml` file:
        1. `${service-name}-core`
        2. `${service-name}-transport`
        3. `${service-name}-domain`
    6. Create the following directory structure:
        1. `src/main/java/momomomo/dungeonwalker/${service-name}/startup`
            1. Create a file named `${camel-cased-service-name}Application.java` inside this directory with the
               following content:
                ```java
                package momomomo.dungeonwalker.${service-name}.startup;

                import org.springframework.boot.SpringApplication;
                import org.springframework.boot.autoconfigure.SpringBootApplication;
                import org.springframework.context.annotation.ComponentScan;

                @SpringBootApplication
                @ComponentScan(basePackages = "momomomo.dungeonwalker.${service-name}")
                public class ${camel-cased-service-name}Application {
                    static void main(String... args) {
                        SpringApplication.run(${camel-cased-service-name}Application.class, args);
                    }
                }
                ```
        2. `src/main/resources`
            1. Create an empty file named `application.yml` inside this directory
        3. `src/test/java/momomomo/dungeonwalker/${service-name}/startup`
            1. Create an empty file named `.gitkeep` inside this directory
        4. `src/test/resources`
            1. Create an empty file named `application.yml` inside this directory

### Build and Run

Use maven to build and run the project, skipping tests.

### Create build scripts

1. Go to `dungeon-walker-docker/build-service-images` directory.
2. Create a file named `build-dungeon-walker-${service-name}.sh` with the following content:
    ```bash
    #!/bin/sh

    docker container stop dungeon-walker-${service-name}
    docker container rm dungeon-walker-${service-name}
    docker image rm alejoceballos/dungeon-walker-${service-name}:v1

    rm -Rf  ~/.m2/repository/momomomo/dungeonwalker/dungeon-walker-${service-name}
    rm -Rf  ~/.m2/repository/momomomo/dungeonwalker/${service-name}-core
    rm -Rf  ~/.m2/repository/momomomo/dungeonwalker/${service-name}-domain
    rm -Rf  ~/.m2/repository/momomomo/dungeonwalker/${service-name}-startup
    rm -Rf  ~/.m2/repository/momomomo/dungeonwalker/${service-name}-transport

    cd ../../dungeon-walker-${service-name} || exit
    mvn clean install jib:dockerBuild -U -DskipTests

    cd ../dungeon-walker-docker/build-service-images || exit
    ```
3. Edit the `build-service-images.sh` file to include the new build script.

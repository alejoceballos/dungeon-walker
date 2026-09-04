# Build Specification

## Objective

Build every listed project successfully, in dependency order, without running
tests.

## Required Command

For every project, run exactly:

```bash
mvn clean install -U -DskipTests
```

Do not substitute another Maven goal or add Maven options.

## Build Order

1. `dungeon-walker-contracts`
2. `dungeon-walker-commons`
3. `dungeon-walker-commons-spring`
4. `dungeon-walker-config-server`
5. `dungeon-walker-discovery-server`
6. `dungeon-walker-engine`
7. `dungeon-walker-ws-server`
8. `dungeon-walker-history`
9. `dungeon-walker-gateway-server`

## Execution Procedure

1. Start from the repository root.
2. Change to the directory of the next project in the build order.
3. Run the required command.
4. Proceed only after that command exits successfully.
5. Stop at the first failed command and report its project directory and Maven
   output. Do not attempt subsequent projects.

## Completion Criteria

The build is complete only when every project in the build-order list has
exited successfully.

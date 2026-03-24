# Dungeon Walker

This is a work in progress.

A dungeon game engine to help me study Kafka, Akka, WebSockets, Microservices and other technologies.

<img src="README.files/Dungeon%20Walker%20Architecture-Overview.png" title="Dungeon Walker Architecture Overview"/>

- All backends are Java-based using Spring Boot
- Communication between services is done through Kafka
- Communication between the client and the services is done through WebSockets
- Spring Cloud is used to manage the services

## The game core

- [dungeon-walker-engine](dungeon-walker-engine/README.md)

Uses Akka to manage asynchronous processes in a clustered environment without worrying about Java threads.
Communicates to the web server through Kafka.

Stalled! Uses React. Connects to the web server through WebSockets to get real-time game events.

## The glue between the client and the game

- [dungeon-walker-ws-server](dungeon-walker-ws-server/README.md)

Uses Websockets to receive and send info to and from a client and kafka to communicate with the core game engine.

## The client

- [dungeon-walker-ui](dungeon-walker-ui/README.md)

Currently, it is just a simple HTML5 log page using vanilla Javascript to display the game events.

## The (Micro) Services Infrastructure

- [dungeon-walker-config-server](dungeon-walker-config-server/README.md)
- [dungeon-walker-discovery-server](dungeon-walker-discovery-server/README.md)

## Support Modules

- [dungeon-walker-commons](dungeon-walker-commons)
- [dungeon-walker-commons-spring](dungeon-walker-commons-spring)
- [dungeon-walker-contracts](dungeon-walker-contracts)

# Run the application

1. Have Docker installed
2. In the [dungeon-walker-docker](dungeon-walker-docker) folder:
   1. Run: `sh` [build-jre-image-with-curl.sh](dungeon-walker-docker/build-jre-image-with-curl.sh)
   2. Run: `sh` [build-n-run-service-images.sh](dungeon-walker-docker/build-n-run-service-images.sh)
3. Open a browser and go to: [http://localhost:8082](http://localhost:8082)
4. You can also check the containers' logs
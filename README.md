# Dungeon Walker

This is a work in progress.

A dungeon game engine to help me study Kafka, Akka, WebSockets, Microservices and other technologies.

<img src="README.files/Dungeon%20Walker%20Architecture-Overview.png" title="Dungeon Walker Architecture Overview"/>

- All backends are Java-based using Spring boot

## The game core

- dungeon-walker-engine

Uses Akka to manage asynchronous processes in a clustered environment without worrying about Java threads.
Communicates to the web server through Kafka.

Stalled! Uses React. Connects to the web server through WebSockets to get real-time game events.

## The glue between the client and the game

- dungeon-walker-ws-server

Uses Websockets to communicate with the web app and kafka to communicate with the core game engine.

## The client

To be defined. Right now I'm using Postman for that.
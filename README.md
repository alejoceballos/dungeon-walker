# Dungeon Walker

This is a work in progress.

A dungeon game engine to help me study Kafka, Akka, React, WebSockets, Microservices and other technologies.

- All backends are Java-based using Spring boot
- The frontend is in React

## The game core

- dungeon-walker-engine

Uses Akka to manage asynchronous processes in a clustered environment without worrying about Java threads. Communicates to the web server through Kafka.

## The game UI

- dungeon-walker-web-app

Uses React. Connects to the web server through WebSockets to get real-time game events.

## The glue between both

- dungeon-walker-ws-server

Uses Websockets to communicate with the web app and kafka to communicate with the core game engine.

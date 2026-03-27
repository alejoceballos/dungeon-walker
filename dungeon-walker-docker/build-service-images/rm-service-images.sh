#!/bin/sh

docker container stop $(docker ps -a -q)
docker container rm $(docker ps -a -q)
docker volume rm $(docker volume ls -q)
docker image rm alejoceballos/dungeon-walker-ui:v1
docker image rm alejoceballos/dungeon-walker-ws-server:v1
docker image rm alejoceballos/dungeon-walker-engine:v1
docker image rm alejoceballos/dungeon-walker-gateway-server:v1
docker image rm alejoceballos/dungeon-walker-discovery-server:v1
docker image rm alejoceballos/dungeon-walker-config-server:v1

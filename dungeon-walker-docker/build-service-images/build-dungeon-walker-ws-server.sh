#!/bin/sh

docker container stop dungeon-walker-ws-server
docker container rm dungeon-walker-ws-server
docker image rm alejoceballos/dungeon-walker-ws-server:v1

cd ../../dungeon-walker-ws-server || exit
mvn clean install jib:dockerBuild -U -DskipTests

cd ../dungeon-walker-docker/build-service-images || exit

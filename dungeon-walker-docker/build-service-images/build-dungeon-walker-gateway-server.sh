#!/bin/sh

docker container stop dungeon-walker-gateway-server
docker container rm dungeon-walker-gateway-server
docker image rm alejoceballos/dungeon-walker-gateway-server:v1

cd ../../dungeon-walker-gateway-server || exit
mvn clean install jib:dockerBuild -U -DskipTests

cd ../dungeon-walker-docker/build-service-images || exit

#!/bin/sh

docker container stop dungeon-walker-discovery-server
docker container rm dungeon-walker-discovery-server
docker image rm alejoceballos/dungeon-walker-discovery-server:v1

cd ../../dungeon-walker-discovery-server || exit
mvn clean install jib:dockerBuild -U -DskipTests

cd ../dungeon-walker-docker/build-service-images || exit

#!/bin/sh

docker container stop dungeon-walker-engine
docker container rm dungeon-walker-engine
docker image rm alejoceballos/dungeon-walker-engine:v1

cd ../../dungeon-walker-engine || exit
mvn clean install jib:dockerBuild -U -DskipTests

cd ../dungeon-walker-docker/build-service-images || exit
